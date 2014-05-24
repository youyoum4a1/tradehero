package com.tradehero.th.fragments.news;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.tradehero.common.persistence.DTOCache;
import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.common.persistence.LiveDTOCache;
import com.tradehero.common.utils.THToast;
import com.tradehero.common.widget.BetterViewAnimator;
import com.tradehero.th.R;
import com.tradehero.th.api.news.NewsItemCompactDTO;
import com.tradehero.th.api.news.key.NewsItemDTOKey;
import com.tradehero.th.api.news.key.NewsItemListKey;
import com.tradehero.th.api.news.key.NewsItemListSecurityKey;
import com.tradehero.th.api.pagination.PaginatedDTO;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.base.DashboardNavigatorActivity;
import com.tradehero.th.base.Navigator;
import com.tradehero.th.fragments.discussion.NewsDiscussionFragment;
import com.tradehero.th.fragments.security.AbstractSecurityInfoFragment;
import com.tradehero.th.persistence.news.NewsItemCompactListCacheNew;
import com.tradehero.th.persistence.security.SecurityCompactCache;
import com.tradehero.th.utils.DaggerUtils;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import timber.log.Timber;

/**
 * Display a ListView of News object for a given SecurityId - It uses
 * the NewsHeadlineCache to get or fetch the news from an abstract provider as needed. In case the
 * news are not in the cache, the download is done in the background using the `fetchSecurityTask`
 * AsyncTask. The task is cancelled when the fragment is paused.
 */
public class NewsHeadlineFragment
        extends AbstractSecurityInfoFragment<SecurityCompactDTO>
{
    @Inject SecurityCompactCache securityCompactCache;
    @Inject NewsItemCompactListCacheNew newsTitleCache;

    @InjectView(R.id.list_news_headline_wrapper) BetterViewAnimator listViewWrapper;
    @InjectView(R.id.list_news_headline) ListView listView;
    @InjectView(R.id.list_news_headline_progressbar) ProgressBar progressBar;

    private DTOCacheNew.Listener<NewsItemListKey, PaginatedDTO<NewsItemCompactDTO>> newsCacheListener;
    private DTOCache.GetOrFetchTask<SecurityId, SecurityCompactDTO> fetchSecurityTask;
    private NewsHeadlineAdapter adapter;
    private PaginatedDTO<NewsItemCompactDTO> paginatedNews;

    public static final String TEST_KEY = "News-Test";
    public static long start = 0;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        DaggerUtils.inject(this);
        newsCacheListener = createNewsCacheListener();
        start = System.currentTimeMillis();
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_news_headline_list, container, false);

        ButterKnife.inject(this, view);
        initViews(view);
        return view;
    }

    private void initViews(View view)
    {
        adapter = new NewsHeadlineAdapter(getActivity(), getActivity().getLayoutInflater(),
                R.layout.news_headline_item_view);

        showLoadingNews();
        if (listView != null)
        {
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l)
                {
                    Object o = adapterView.getItemAtPosition(position);
                    if (o instanceof NewsItemDTOKey)
                    {
                        handleNewsClicked(position, (NewsItemDTOKey) o);
                    }
                }
            });
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
        detachFetchTask();
        newsTitleCache.unregister(newsCacheListener);

        if (listView != null)
        {
            listView.setOnItemClickListener(null);
        }
        listView = null;
        adapter = null;

        super.onDestroyView();
    }

    @Override public void onDestroy()
    {
        newsCacheListener = null;
        super.onDestroy();
    }

    @Override protected LiveDTOCache<SecurityId, SecurityCompactDTO> getInfoCache()
    {
        return securityCompactCache;
    }

    @Override public void linkWith(SecurityId securityId, boolean andDisplay)
    {
        super.linkWith(securityId, andDisplay);

        fetchSecurity(securityId);
        if (this.securityId != null)
        {
            fetchSecurityNews();
        }
    }

    public void linkWith(SecurityCompactDTO securityCompactDTO, boolean andDisplay)
    {
        value = securityCompactDTO;

        if (this.value != null)
        {
            fetchSecurityNews();
        }
    }

    protected void detachFetchTask()
    {
        if (fetchSecurityTask != null)
        {
            fetchSecurityTask.setListener(null);
        }
        fetchSecurityTask = null;
    }

    protected void fetchSecurity(SecurityId securityId)
    {
        detachFetchTask();
        fetchSecurityTask = getInfoCache().getOrFetch(securityId, this);
        fetchSecurityTask.execute();
    }

    private void fetchSecurityNews()
    {
        Timber.d("%s fetchSecurityNews,consume: %s",TEST_KEY,(System.currentTimeMillis() - start));
        detachFetchTask();

        NewsItemListKey listKey = new NewsItemListSecurityKey(value.getSecurityIntegerId(), null, null);
        newsTitleCache.register(listKey, newsCacheListener);
        newsTitleCache.getOrFetchAsync(listKey, false);
    }

    @Override public void display()
    {
        Timber.d("%s display consume: %s",TEST_KEY,(System.currentTimeMillis() - start));
        displayNewsListView();

        showNewsList();
    }

    @Override public void onErrorThrown(SecurityId securityId, Throwable error)
    {
        super.onErrorThrown(securityId, error);
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
                    newsItemDTOKeyList.add(newsItemDTO.createDTOKey());
                }
            }

            adapter.setItems(newsItemDTOKeyList);
            adapter.notifyDataSetChanged();
        }
    }

    protected void handleNewsClicked(int position, NewsItemDTOKey news)
    {
        if (news != null)
        {
            int resId = adapter.getBackgroundRes(position);
            Bundle bundle = new Bundle();
            bundle.putInt(NewsDiscussionFragment.BUNDLE_KEY_TITLE_BACKGROUND_RES, resId);
            bundle.putString(NewsDiscussionFragment.BUNDLE_KEY_SECURITY_SYMBOL,
                    securityId.securitySymbol);
            bundle.putBundle(NewsDiscussionFragment.DISCUSSION_KEY_BUNDLE_KEY,news.getArgs());
            getNavigator().pushFragment(NewsDiscussionFragment.class, bundle);

        }
    }

    private Navigator getNavigator()
    {
        return ((DashboardNavigatorActivity) getActivity()).getDashboardNavigator();
    }

    protected DTOCacheNew.Listener<NewsItemListKey, PaginatedDTO<NewsItemCompactDTO>> createNewsCacheListener()
    {
        return new NewsHeadlineNewsListListener();
    }

    protected class NewsHeadlineNewsListListener implements DTOCacheNew.HurriedListener<NewsItemListKey, PaginatedDTO<NewsItemCompactDTO>>
    {
        @Override public void onPreCachedDTOReceived(NewsItemListKey key,
                PaginatedDTO<NewsItemCompactDTO> value)
        {
            linkWith(value, true);
        }

        @Override public void onDTOReceived(NewsItemListKey key,
                PaginatedDTO<NewsItemCompactDTO> value)
        {
            linkWith(value, true);
        }

        @Override public void onErrorThrown(NewsItemListKey key, Throwable error)
        {
            THToast.show(R.string.error_fetch_security_info);
        }
    }
}
