package com.tradehero.th.fragments.discovery;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnItemClick;
import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.common.utils.THToast;
import com.tradehero.common.widget.BetterViewAnimator;
import com.tradehero.th.BottomTabsQuickReturnListViewListener;
import com.tradehero.th.R;
import com.tradehero.th.api.news.NewsItemCompactDTO;
import com.tradehero.th.api.news.NewsItemDTO;
import com.tradehero.th.api.news.key.NewsItemDTOKey;
import com.tradehero.th.api.news.key.NewsItemListFeaturedKey;
import com.tradehero.th.api.news.key.NewsItemListGlobalKey;
import com.tradehero.th.api.news.key.NewsItemListKey;
import com.tradehero.th.api.pagination.PaginatedDTO;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.web.WebViewFragment;
import com.tradehero.th.inject.HierarchyInjector;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.persistence.discussion.DiscussionCache;
import com.tradehero.th.persistence.news.NewsItemCompactListCacheNew;
import com.tradehero.th.widget.MultiScrollListener;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;

public class NewsHeadlineFragment extends Fragment
{
    private static final String NEWS_TYPE_KEY = NewsCarouselFragment.class.getName() + ".newsType";

    @InjectView(R.id.content_wrapper) BetterViewAnimator mContentWrapper;
    @InjectView(android.R.id.list) ListView mNewsListView;
    @OnItemClick(android.R.id.list) void handleNewsItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        NewsItemDTOKey newsItemDTOKey = (NewsItemDTOKey) parent.getItemAtPosition(position);
        NewsItemDTO newsItemDTO = (NewsItemDTO) discussionCache.get(newsItemDTOKey);

        if (newsItemDTO != null && newsItemDTO.url != null)
        {
            Bundle bundle = new Bundle();
            WebViewFragment.putUrl(bundle, newsItemDTO.url);
            navigator.pushFragment(WebViewFragment.class, bundle);
        }
    }

    @Inject DashboardNavigator navigator;
    @Inject DiscussionCache discussionCache;
    @Inject NewsItemCompactListCacheNew newsItemCompactListCache;
    @Inject @BottomTabsQuickReturnListViewListener AbsListView.OnScrollListener dashboardBottomTabsScrollListener;

    private int mDisplayedViewId;
    private DTOCacheNew.Listener<NewsItemListKey, PaginatedDTO<NewsItemCompactDTO>> mFeaturedNewsListener;
    private NewsHeadlineAdapter mFeaturedNewsAdapter;
    protected NewsItemListKey newsItemListKey;
    private AbsListView.OnScrollListener scrollListener;

    public NewsHeadlineFragment()
    {
        super();

        Bundle args = getArguments();
        if (args != null)
        {
            int newsTypeOrdinal = args.getInt(NEWS_TYPE_KEY);
            if (newsTypeOrdinal >= 0 && newsTypeOrdinal < NewsType.values().length)
            {
                newsItemListKey = newsItemListKeyFromNewsType(NewsType.values()[newsTypeOrdinal]);
            }
        }
    }

    private NewsItemListKey newsItemListKeyFromNewsType(NewsType newsType)
    {
        switch (newsType)
        {
            case MotleyFool:
                return new NewsItemListFeaturedKey(null, null);
            case Global:
                return new NewsItemListGlobalKey(null, null);
            default:
                return null;
        }
    }

    public static NewsHeadlineFragment newInstance(NewsType newsType)
    {
        if (newsType == NewsType.Region)
        {
            return new RegionalNewsHeadlineFragment();
        }

        NewsHeadlineFragment newsHeadlineFragment = new NewsHeadlineFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(NEWS_TYPE_KEY, newsType.ordinal());
        newsHeadlineFragment.setArguments(bundle);
        return newsHeadlineFragment;
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.discovery_featured_news, container, false);
        initView(view);
        return view;
    }

    private void initView(View view)
    {
        ButterKnife.inject(this, view);

        mFeaturedNewsListener = new FeaturedNewsListener();

        mFeaturedNewsAdapter = new NewsHeadlineAdapter(getActivity(), R.layout.news_headline_item_view);

        View paddingHeader = new View(getActivity());
        paddingHeader.setLayoutParams(new AbsListView.LayoutParams(1, getResources().getDimensionPixelOffset(R.dimen.discovery_news_carousel_height)));
        mNewsListView.addHeaderView(paddingHeader);
        mNewsListView.setAdapter(mFeaturedNewsAdapter);
        mNewsListView.setOnScrollListener(new MultiScrollListener(scrollListener, dashboardBottomTabsScrollListener));
    }

    public void setScrollListener(AbsListView.OnScrollListener scrollListener)
    {
        this.scrollListener = scrollListener;
    }

    @Override public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        HierarchyInjector.inject(this);
    }

    @Override public void onResume()
    {
        super.onResume();
        if (mDisplayedViewId > 0)
        {
            mContentWrapper.setDisplayedChildByLayoutId(mDisplayedViewId);
        }

        refreshNews();
    }

    protected void refreshNews()
    {
        detachFetchFeaturedNewsTask();
        newsItemCompactListCache.register(newsItemListKey, mFeaturedNewsListener);
        newsItemCompactListCache.getOrFetchAsync(newsItemListKey);
    }

    private void detachFetchFeaturedNewsTask()
    {
        newsItemCompactListCache.unregister(mFeaturedNewsListener);
    }

    @Override public void onPause()
    {
        mDisplayedViewId = mContentWrapper.getDisplayedChildLayoutId();
        super.onPause();
    }

    private class FeaturedNewsListener implements DTOCacheNew.Listener<NewsItemListKey,PaginatedDTO<NewsItemCompactDTO>>
    {
        @Override public void onDTOReceived(@NotNull NewsItemListKey key, @NotNull PaginatedDTO<NewsItemCompactDTO> value)
        {
            linkWith(value, true);
        }

        @Override public void onErrorThrown(@NotNull NewsItemListKey key, @NotNull Throwable error)
        {
            THToast.show(new THException(error));
        }
    }

    private void linkWith(PaginatedDTO<NewsItemCompactDTO> value, boolean display)
    {
        List<NewsItemCompactDTO> newsItemCompactDTOs = value.getData();
        List<NewsItemDTOKey> newsItemDTOKeys = new ArrayList<>();
        for (NewsItemCompactDTO newsItemCompactDTO: newsItemCompactDTOs)
        {
            newsItemDTOKeys.add(newsItemCompactDTO.getDiscussionKey());
        }

        mFeaturedNewsAdapter.clear();
        mFeaturedNewsAdapter.addAll(newsItemDTOKeys);
        mFeaturedNewsAdapter.notifyDataSetChanged();

        mContentWrapper.setDisplayedChildByLayoutId(mNewsListView.getId());
    }
}
