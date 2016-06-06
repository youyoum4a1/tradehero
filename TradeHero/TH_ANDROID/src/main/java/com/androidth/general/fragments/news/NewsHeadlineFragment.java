package com.androidth.general.fragments.news;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnItemClick;
import com.androidth.general.common.rx.PairGetSecond;
import com.androidth.general.common.widget.BetterViewAnimator;
import com.androidth.general.R;
import com.androidth.general.api.news.NewsItemCompactDTO;
import com.androidth.general.api.news.key.NewsItemListKey;
import com.androidth.general.api.news.key.NewsItemListSecurityKey;
import com.androidth.general.api.pagination.PaginatedDTO;
import com.androidth.general.api.security.SecurityCompactDTO;
import com.androidth.general.api.security.SecurityId;
import com.androidth.general.fragments.DashboardNavigator;
import com.androidth.general.fragments.base.FragmentOuterElements;
import com.androidth.general.fragments.discussion.AbstractDiscussionCompactItemViewLinear;
import com.androidth.general.fragments.discussion.AbstractDiscussionCompactItemViewLinearDTOFactory;
import com.androidth.general.fragments.discussion.DiscussionFragmentUtil;
import com.androidth.general.fragments.discussion.NewsDiscussionFragment;
import com.androidth.general.fragments.security.AbstractSecurityInfoFragment;
import com.androidth.general.inject.HierarchyInjector;
import com.androidth.general.models.discussion.UserDiscussionAction;
import com.androidth.general.persistence.news.NewsItemCompactListCacheRx;
import com.androidth.general.persistence.security.SecurityCompactCacheRx;
import com.androidth.general.rx.TimberOnErrorAction1;
import com.androidth.general.rx.ToastOnErrorAction1;
import com.androidth.general.utils.metrics.AnalyticsConstants;
import dagger.Lazy;
import java.util.List;
import javax.inject.Inject;
import rx.Observable;
import rx.android.app.AppObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Display a ListView of News object for a given SecurityId - It uses the NewsHeadlineCache to get or fetch the news from an abstract provider as
 * needed. In case the news are not in the cache, the download is done in the background using the `fetchSecurityTask` AsyncTask. The task is
 * cancelled when the fragment is paused.
 */
public class NewsHeadlineFragment extends AbstractSecurityInfoFragment
{
    @Inject SecurityCompactCacheRx securityCompactCache;
    @Inject NewsItemCompactListCacheRx newsTitleCache;
    @Inject Lazy<DashboardNavigator> navigator;
    @Inject AbstractDiscussionCompactItemViewLinearDTOFactory viewDTOFactory;
    @Inject DiscussionFragmentUtil discussionFragmentUtil;
    @Inject FragmentOuterElements fragmentElements;

    @Bind(R.id.list_news_headline_wrapper) BetterViewAnimator listViewWrapper;
    @Bind(R.id.list_news_headline) ListView listView;
    @Bind(R.id.list_news_headline_progressbar) ProgressBar progressBar;

    private NewsHeadlineAdapter adapter;

    @Override public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        adapter = new NewsHeadlineAdapter(activity, R.layout.news_headline_item_view);
    }

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        HierarchyInjector.inject(this);
    }

    @Override public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_news_headline_list, container, false);
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        listView.setAdapter(adapter);
        listView.setOnScrollListener(fragmentElements.getListViewScrollListener());
        fetchSecurityNews();
        registerUserActions();
    }

    @Override public void onDestroyView()
    {
        if (listView != null)
        {
            listView.setOnScrollListener(null);
        }
        ButterKnife.unbind(this);
        super.onDestroyView();
    }

    @Override public void onDetach()
    {
        adapter = null;
        super.onDetach();
    }

    private void fetchSecurityNews()
    {
        if (adapter.getCount() == 0)
        {
            listViewWrapper.setDisplayedChildByLayoutId(progressBar.getId());
        }
        if (securityId != null)
        {
            onDestroyViewSubscriptions.add(
                    securityCompactCache.getOne(securityId)
                            .subscribeOn(Schedulers.computation())
                            .flatMap(new Func1<Pair<SecurityId, SecurityCompactDTO>, Observable<PaginatedDTO<NewsItemCompactDTO>>>()
                            {
                                @Override public Observable<PaginatedDTO<NewsItemCompactDTO>> call(Pair<SecurityId, SecurityCompactDTO> pair)
                                {
                                    securityCompactDTO = pair.second;
                                    return newsTitleCache.get(new NewsItemListSecurityKey(
                                            pair.second.getSecurityIntegerId(),
                                            null, null))
                                            .map(new PairGetSecond<NewsItemListKey, PaginatedDTO<NewsItemCompactDTO>>());
                                }
                            })
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
                                    new ToastOnErrorAction1(getString(R.string.error_fetch_security_info))));
        }
    }

    protected void registerUserActions()
    {
        onDestroyViewSubscriptions.add(AppObservable.bindSupportFragment(
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
                        new TimberOnErrorAction1("When registering actions")));
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
            NewsWebFragment.putNewsId(bundle, news.id);
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
