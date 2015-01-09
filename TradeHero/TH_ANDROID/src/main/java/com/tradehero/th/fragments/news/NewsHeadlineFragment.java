package com.tradehero.th.fragments.news;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnItemClick;
import com.tradehero.common.utils.THToast;
import com.tradehero.common.widget.BetterViewAnimator;
import com.tradehero.th.BottomTabsQuickReturnListViewListener;
import com.tradehero.th.R;
import com.tradehero.th.api.discussion.AbstractDiscussionCompactDTO;
import com.tradehero.th.api.discussion.AbstractDiscussionDTO;
import com.tradehero.th.api.news.NewsItemCompactDTO;
import com.tradehero.th.api.news.key.NewsItemDTOKey;
import com.tradehero.th.api.news.key.NewsItemListKey;
import com.tradehero.th.api.news.key.NewsItemListSecurityKey;
import com.tradehero.th.api.pagination.PaginatedDTO;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.discussion.NewsDiscussionFragment;
import com.tradehero.th.fragments.security.AbstractSecurityInfoFragment;
import com.tradehero.th.fragments.web.WebViewFragment;
import com.tradehero.th.inject.HierarchyInjector;
import com.tradehero.th.persistence.discussion.DiscussionCacheRx;
import com.tradehero.th.persistence.news.NewsItemCompactListCacheRx;
import com.tradehero.th.persistence.security.SecurityCompactCacheRx;
import dagger.Lazy;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import rx.Observer;
import rx.Subscription;
import rx.android.observables.AndroidObservable;
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
    @Inject protected DiscussionCacheRx discussionCache;
    @Inject Lazy<DashboardNavigator> navigator;
    @InjectView(R.id.list_news_headline_wrapper) BetterViewAnimator listViewWrapper;
    @InjectView(R.id.list_news_headline) ListView listView;
    @InjectView(R.id.list_news_headline_progressbar) ProgressBar progressBar;

    @Inject @BottomTabsQuickReturnListViewListener AbsListView.OnScrollListener dashboardTabListViewScrollListener;

    private NewsHeadlineAdapter adapter;
    private PaginatedDTO<NewsItemCompactDTO> paginatedNews;

    @Nullable Subscription securitySubscription;
    @Nullable Subscription securityNewsSubscription;

    public static final String TEST_KEY = "News-Test";
    public static long start = 0;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        HierarchyInjector.inject(this);
        start = System.currentTimeMillis();
        adapter = new NewsHeadlineAdapter(getActivity(),
                R.layout.news_headline_item_view);
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
        listView.setOnScrollListener(dashboardTabListViewScrollListener);
    }

    @Override public void onStop()
    {
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

    @Override public void onDestroy()
    {
        adapter = null;
        super.onDestroy();
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

    @Override public void linkWith(@NonNull SecurityId securityId, boolean andDisplay)
    {
        super.linkWith(securityId, andDisplay);
        fetchSecurity(securityId);
    }

    protected void fetchSecurity(@NonNull SecurityId securityId)
    {
        unsubscribe(securitySubscription);
        securitySubscription = AndroidObservable.bindFragment(this, securityCompactCache.get(securityId))
                .subscribe(new Observer<Pair<SecurityId, SecurityCompactDTO>>()
                {
                    @Override public void onCompleted()
                    {
                    }

                    @Override public void onError(Throwable e)
                    {
                    }

                    @Override public void onNext(Pair<SecurityId, SecurityCompactDTO> pair)
                    {
                        linkWith(pair.second, !isDetached());
                    }
                });
    }

    public void linkWith(SecurityCompactDTO securityCompactDTO, boolean andDisplay)
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
        securityNewsSubscription = AndroidObservable.bindFragment(this, newsTitleCache.get(listKey))
                .subscribe(
                        pair -> linkWith(pair.second, true),
                        e -> THToast.show(R.string.error_fetch_security_info));
    }

    public void linkWith(PaginatedDTO<NewsItemCompactDTO> news, boolean andDisplay)
    {
        paginatedNews = news;

        if (andDisplay)
        {
            displayNewsListView();
            showNewsList();
        }
    }

    @Override public void display()
    {
        Timber.d("%s display consume: %s", TEST_KEY, (System.currentTimeMillis() - start));
        displayNewsListView();

        showNewsList();
    }

    public void displayNewsListView()
    {
        if (!isDetached() && adapter != null && paginatedNews != null)
        {
            List<NewsItemCompactDTO> data = paginatedNews.getData();
            List<NewsItemDTOKey> newsItemDTOKeyList = new ArrayList<>();

            if (data != null)
            {
                for (NewsItemCompactDTO newsItemDTO : data)
                {
                    newsItemDTOKeyList.add(newsItemDTO.getDiscussionKey());
                }
            }
            adapter.setSecurityId(securityId);
            adapter.setItems(data);
            adapter.notifyDataSetChanged();
        }
    }

    @SuppressWarnings({"UnusedDeclaration", "UnusedParameters"})
    @OnItemClick(R.id.list_news_headline)
    protected void listItemClicked(AdapterView<?> parent, View view, int position, long id)
    {
        Object o = parent.getItemAtPosition(position);
        Bundle bundle = new Bundle();
        if (o instanceof NewsItemCompactDTO && ((NewsItemCompactDTO) o).url != null)
        {
            WebViewFragment.putUrl(bundle, ((NewsItemCompactDTO) o).url);
            navigator.get().pushFragment(WebViewFragment.class, bundle);
        }
        else if (o instanceof NewsItemCompactDTO)
        {
            NewsDiscussionFragment.putSecuritySymbol(bundle, ((NewsItemCompactDTO) o).topReferencedSecurity.getExchangeSymbol());
            NewsDiscussionFragment.putDiscussionKey(bundle, ((AbstractDiscussionCompactDTO) o).getDiscussionKey());
            navigator.get().pushFragment(NewsDiscussionFragment.class, bundle);
        }
    }
}
