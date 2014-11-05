package com.tradehero.th.fragments.news;

import android.os.Bundle;
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
import com.tradehero.common.utils.THToast;
import com.tradehero.common.widget.BetterViewAnimator;
import com.tradehero.th.BottomTabsQuickReturnListViewListener;
import com.tradehero.th.R;
import com.tradehero.th.api.discussion.AbstractDiscussionCompactDTO;
import com.tradehero.th.api.discussion.key.DiscussionKey;
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
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.persistence.discussion.DiscussionCacheRx;
import com.tradehero.th.persistence.news.NewsItemCompactListCacheRx;
import com.tradehero.th.persistence.security.SecurityCompactCacheRx;
import dagger.Lazy;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import rx.Observer;
import rx.android.observables.AndroidObservable;
import rx.android.schedulers.AndroidSchedulers;
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

    protected AbstractDiscussionCompactDTO abstractDiscussionCompactDTO;

    public static final String TEST_KEY = "News-Test";
    public static long start = 0;

    private int tempPosition = 0;
    private NewsItemDTOKey tempDto = null;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        HierarchyInjector.inject(this);
        start = System.currentTimeMillis();
    }

    @Override public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_news_headline_list, container, false);

        ButterKnife.inject(this, view);
        initViews(view);
        return view;
    }

    protected Observer<Pair<DiscussionKey, AbstractDiscussionCompactDTO>> createDiscussionFetchObserver()
    {
        return new DiscussionFetchObserver();
    }

    private class DiscussionFetchObserver
            implements Observer<Pair<DiscussionKey, AbstractDiscussionCompactDTO>>
    {
        @Override public void onNext(Pair<DiscussionKey, AbstractDiscussionCompactDTO> pair)
        {
            linkWith(pair.second, true);
        }

        @Override public void onCompleted()
        {
        }

        @Override public void onError(Throwable e)
        {
            THToast.show(new THException(e));
        }
    }

    protected void linkWith(AbstractDiscussionCompactDTO abstractDiscussionDTO, boolean andDisplay)
    {
        this.abstractDiscussionCompactDTO = abstractDiscussionDTO;
        Bundle bundle = new Bundle();
        if (abstractDiscussionCompactDTO != null
                && ((NewsItemCompactDTO) abstractDiscussionCompactDTO).url != null)
        {
            WebViewFragment.putUrl(bundle, ((NewsItemCompactDTO) abstractDiscussionCompactDTO).url);
            navigator.get().pushFragment(WebViewFragment.class, bundle);
        }
        else
        {
            handleNewClicked(tempPosition, tempDto);
        }
    }

    private void initViews(View view)
    {
        adapter = new NewsHeadlineAdapter(getActivity(),
                R.layout.news_headline_item_view);

        showLoadingNews();
        if (listView != null)
        {
            listView.setAdapter(adapter);
            listView.setOnItemClickListener((adapterView, view1, position, l) -> {
                Object o = adapterView.getItemAtPosition(position);
                if (o instanceof NewsItemDTOKey)
                {
                    handleNewsClicked(position, (NewsItemDTOKey) o);
                }
            });
            listView.setOnScrollListener(dashboardTabListViewScrollListener);
        }
    }

    private void showNewsList()
    {
        listViewWrapper.setDisplayedChildByLayoutId(listView.getId());
    }

    private void showLoadingNews()
    {
        listViewWrapper.setDisplayedChildByLayoutId(progressBar.getId());
    }

    @Override public void onDestroyView()
    {
        if (listView != null)
        {
            listView.setOnItemClickListener(null);
            listView.setOnScrollListener(null);
        }
        listView = null;
        adapter = null;

        super.onDestroyView();
    }

    @Override protected SecurityCompactCacheRx getInfoCache()
    {
        return securityCompactCache;
    }

    @Override public void linkWith(@NotNull SecurityId securityId, boolean andDisplay)
    {
        super.linkWith(securityId, andDisplay);
        fetchSecurity(securityId);
    }

    public void linkWith(SecurityCompactDTO securityCompactDTO, boolean andDisplay)
    {
        value = securityCompactDTO;

        if (this.value != null)
        {
            fetchSecurityNews();
        }
    }

    protected void fetchSecurity(@NotNull SecurityId securityId)
    {
        AndroidObservable.bindFragment(this, securityCompactCache.get(securityId))
                .observeOn(AndroidSchedulers.mainThread())
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

    private void fetchSecurityNews()
    {
        Timber.d("%s fetchSecurityNews,consume: %s", TEST_KEY, (System.currentTimeMillis() - start));

        NewsItemListKey listKey = new NewsItemListSecurityKey(value.getSecurityIntegerId(), null, null);
        AndroidObservable.bindFragment(this, newsTitleCache.get(listKey))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createNewsCacheObserver());
    }

    @Override public void display()
    {
        Timber.d("%s display consume: %s", TEST_KEY, (System.currentTimeMillis() - start));
        displayNewsListView();

        showNewsList();
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
            adapter.setItems(newsItemDTOKeyList);
            adapter.notifyDataSetChanged();
        }
    }

    protected void handleNewsClicked(int position, @Nullable NewsItemDTOKey news)
    {
        tempPosition = position;
        tempDto = news;
        if (news != null)
        {
            fetchDiscussionDetail(true, news);
        }
    }

    protected void handleNewClicked(int position, NewsItemDTOKey news)
    {
        if (news != null)
        {
            int resId = adapter.getBackgroundRes(position);
            Bundle bundle = new Bundle();
            NewsDiscussionFragment.putBackgroundResId(bundle, resId);
            NewsDiscussionFragment.putSecuritySymbol(bundle, securityId.getSecuritySymbol());
            NewsDiscussionFragment.putDiscussionKey(bundle, news);
            navigator.get().pushFragment(NewsDiscussionFragment.class, bundle);
        }
    }

    private void fetchDiscussionDetail(boolean force, NewsItemDTOKey discussionKey)
    {
        AndroidObservable.bindFragment(this, discussionCache.get(discussionKey))
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(createDiscussionFetchObserver());
    }

    @NotNull protected Observer<Pair<NewsItemListKey, PaginatedDTO<NewsItemCompactDTO>>> createNewsCacheObserver()
    {
        return new NewsHeadlineNewsListObserver();
    }

    protected class NewsHeadlineNewsListObserver implements Observer<Pair<NewsItemListKey, PaginatedDTO<NewsItemCompactDTO>>>
    {
        @Override public void onNext(Pair<NewsItemListKey, PaginatedDTO<NewsItemCompactDTO>> pair)
        {
            linkWith(pair.second, true);
        }

        @Override public void onCompleted()
        {
        }

        @Override public void onError(Throwable e)
        {
            THToast.show(R.string.error_fetch_security_info);
        }
    }
}
