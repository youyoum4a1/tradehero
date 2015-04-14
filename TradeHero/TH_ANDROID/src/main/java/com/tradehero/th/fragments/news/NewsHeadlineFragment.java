package com.tradehero.th.fragments.news;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnItemClick;
import com.tradehero.common.rx.PairGetSecond;
import com.tradehero.common.widget.BetterViewAnimator;
import com.tradehero.th.R;
import com.tradehero.th.api.news.NewsItemCompactDTO;
import com.tradehero.th.api.news.key.NewsItemListKey;
import com.tradehero.th.api.news.key.NewsItemListSecurityKey;
import com.tradehero.th.api.pagination.PaginatedDTO;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.base.FragmentOuterElements;
import com.tradehero.th.fragments.discussion.AbstractDiscussionCompactItemViewLinear;
import com.tradehero.th.fragments.discussion.AbstractDiscussionCompactItemViewLinearDTOFactory;
import com.tradehero.th.fragments.discussion.DiscussionFragmentUtil;
import com.tradehero.th.fragments.discussion.NewsDiscussionFragment;
import com.tradehero.th.fragments.security.AbstractSecurityInfoFragment;
import com.tradehero.th.inject.HierarchyInjector;
import com.tradehero.th.models.discussion.UserDiscussionAction;
import com.tradehero.th.persistence.news.NewsItemCompactListCacheRx;
import com.tradehero.th.persistence.security.SecurityCompactCacheRx;
import com.tradehero.th.rx.EmptyAction1;
import com.tradehero.th.rx.TimberOnErrorAction;
import com.tradehero.th.rx.ToastAction;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import dagger.Lazy;
import java.util.List;
import javax.inject.Inject;
import rx.Observable;
import rx.Subscription;
import rx.android.app.AppObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.internal.util.SubscriptionList;
import timber.log.Timber;

/**
 * Display a ListView of News object for a given SecurityId - It uses the NewsHeadlineCache to get or fetch the news from an abstract provider as
 * needed. In case the news are not in the cache, the download is done in the background using the `fetchSecurityTask` AsyncTask. The task is
 * cancelled when the fragment is paused.
 */
public class NewsHeadlineFragment extends AbstractSecurityInfoFragment<SecurityCompactDTO>
{
    @Inject SecurityCompactCacheRx securityCompactCache;
    @Inject NewsItemCompactListCacheRx newsTitleCache;
    @Inject Lazy<DashboardNavigator> navigator;
    @Inject AbstractDiscussionCompactItemViewLinearDTOFactory viewDTOFactory;
    @Inject DiscussionFragmentUtil discussionFragmentUtil;
    @Inject FragmentOuterElements fragmentElements;
    @InjectView(R.id.list_news_headline_wrapper) BetterViewAnimator listViewWrapper;
    @InjectView(R.id.list_news_headline) ListView listView;
    @InjectView(R.id.list_news_headline_progressbar) ProgressBar progressBar;

    private NewsHeadlineAdapter adapter;

    @Nullable Subscription securitySubscription;
    @Nullable Subscription securityNewsSubscription;
    protected SubscriptionList onStopSubscriptions;

    public static final String TEST_KEY = "News-Test";
    public static long start = 0;

    @Override public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        adapter = new NewsHeadlineAdapter(activity, R.layout.news_headline_item_view);
    }

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        HierarchyInjector.inject(this);
        start = System.currentTimeMillis();
    }

    @Override public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_news_headline_list, container, false);
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);
        showLoadingNews();
        listView.setAdapter(adapter);
        listView.setOnScrollListener(fragmentElements.getListViewScrollListener());
    }

    @Override public void onStart()
    {
        super.onStart();
        onStopSubscriptions = new SubscriptionList();
        registerUserActions();
    }

    @Override public void onStop()
    {
        onStopSubscriptions.unsubscribe();
        unsubscribe(securitySubscription);
        securitySubscription = null;
        unsubscribe(securityNewsSubscription);
        securityNewsSubscription = null;
        super.onStop();
    }

    @Override public void onDestroyView()
    {
        if (listView != null)
        {
            listView.setOnScrollListener(null);
        }
        ButterKnife.reset(this);
        super.onDestroyView();
    }

    @Override public void onDetach()
    {
        adapter = null;
        super.onDetach();
    }

    private void showNewsList()
    {
        listViewWrapper.setDisplayedChildByLayoutId(listView.getId());
    }

    private void showLoadingNews()
    {
        listViewWrapper.setDisplayedChildByLayoutId(progressBar.getId());
    }

    @Override protected SecurityCompactCacheRx getInfoCache()
    {
        return securityCompactCache;
    }

    @Override public void linkWith(@Nullable SecurityId securityId)
    {
        super.linkWith(securityId);
        if (securityId != null)
        {
            fetchSecurity(securityId);
        }
    }

    protected void fetchSecurity(@NonNull SecurityId securityId)
    {
        unsubscribe(securitySubscription);
        securitySubscription = AppObservable.bindFragment(
                this,
                securityCompactCache.get(securityId))
                .map(new PairGetSecond<SecurityId, SecurityCompactDTO>())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Action1<SecurityCompactDTO>()
                        {
                            @Override public void call(SecurityCompactDTO compactDTO)
                            {
                                linkWith(compactDTO);
                            }
                        },
                        new EmptyAction1<Throwable>());
    }

    public void linkWith(SecurityCompactDTO securityCompactDTO)
    {
        value = securityCompactDTO;

        if (this.value != null)
        {
            fetchSecurityNews();
        }
    }

    private void fetchSecurityNews()
    {
        Timber.d("%s fetchSecurityNews,consume: %s", TEST_KEY, (System.currentTimeMillis() - start));

        unsubscribe(securityNewsSubscription);
        NewsItemListKey listKey = new NewsItemListSecurityKey(value.getSecurityIntegerId(), null, null);
        securityNewsSubscription = AppObservable.bindFragment(
                this,
                newsTitleCache.get(listKey))
                .map(new PairGetSecond<NewsItemListKey, PaginatedDTO<NewsItemCompactDTO>>())
                .flatMap(new Func1<PaginatedDTO<NewsItemCompactDTO>, Observable<List<AbstractDiscussionCompactItemViewLinear.DTO>>>()
                {
                    @Override public Observable<List<AbstractDiscussionCompactItemViewLinear.DTO>> call(
                            PaginatedDTO<NewsItemCompactDTO> newsItemCompactDTOPaginatedDTO)
                    {
                        List<NewsItemCompactDTO> newsItems = newsItemCompactDTOPaginatedDTO.getData();
                        if (newsItems == null)
                        {
                            return Observable.empty();
                        }
                        return viewDTOFactory.createNewsHeadlineViewLinearDTOs(newsItems);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Action1<List<AbstractDiscussionCompactItemViewLinear.DTO>>()
                        {
                            @Override public void call(List<AbstractDiscussionCompactItemViewLinear.DTO> dtos)
                            {
                                adapter.setItems(dtos);
                                listViewWrapper.setDisplayedChildByLayoutId(listView.getId());
                            }
                        },
                        new ToastAction<Throwable>(getString(R.string.error_fetch_security_info)));
    }

    protected void registerUserActions()
    {
        onStopSubscriptions.add(AppObservable.bindFragment(
                this,
                adapter.getUserActionObservable())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(new Func1<UserDiscussionAction, Observable<UserDiscussionAction>>()
                {
                    @Override public Observable<UserDiscussionAction> call(UserDiscussionAction userDiscussionAction)
                    {
                        return discussionFragmentUtil.handleUserAction(getActivity(), userDiscussionAction);
                    }
                })
                .subscribe(
                        new Action1<UserDiscussionAction>()
                        {
                            @Override public void call(UserDiscussionAction userDiscussionAction)
                            {
                                Timber.e(new Exception(), "Unhandled " + userDiscussionAction);
                            }
                        },
                        new TimberOnErrorAction("When registering actions")));
    }

    @Override public void display()
    {
        Timber.d("%s display consume: %s", TEST_KEY, (System.currentTimeMillis() - start));
        showNewsList();
    }

    @SuppressWarnings({"UnusedDeclaration", "UnusedParameters"})
    @OnItemClick(R.id.list_news_headline)
    protected void listItemClicked(AdapterView<?> parent, View view, int position, long id)
    {
        NewsItemCompactDTO news = (NewsItemCompactDTO) ((NewsHeadlineViewLinear.DTO) parent.getItemAtPosition(position)).viewHolderDTO.discussionDTO;
        Bundle bundle = new Bundle();
        if (news.url != null)
        {
            NewsWebFragment.putUrl(bundle, news.url);
            NewsWebFragment.putPreviousScreen(bundle, AnalyticsConstants.NewsSecurityScreen);
            navigator.get().pushFragment(NewsWebFragment.class, bundle);
        }
        else
        {
            if (news.topReferencedSecurity != null)
            {
                NewsDiscussionFragment.putSecuritySymbol(bundle, news.topReferencedSecurity.getExchangeSymbol());
            }
            NewsDiscussionFragment.putDiscussionKey(bundle, news.getDiscussionKey());
            navigator.get().pushFragment(NewsDiscussionFragment.class, bundle);
        }
    }
}
