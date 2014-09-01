package com.tradehero.th.fragments.discovery;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ProgressBar;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.actionbarsherlock.app.SherlockFragment;
import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.common.utils.THToast;
import com.tradehero.common.widget.BetterViewAnimator;
import com.tradehero.th.R;
import com.tradehero.th.api.news.NewsItemCompactDTO;
import com.tradehero.th.api.news.key.NewsItemDTOKey;
import com.tradehero.th.api.news.key.NewsItemListFeaturedKey;
import com.tradehero.th.api.news.key.NewsItemListKey;
import com.tradehero.th.api.pagination.PaginatedDTO;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.persistence.news.NewsItemCompactListCacheNew;
import com.tradehero.th.utils.DaggerUtils;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;

public class FeaturedNewsHeadlineFragment extends SherlockFragment
{
    @InjectView(R.id.content_wrapper) BetterViewAnimator mContentWrapper;
    @InjectView(android.R.id.list) AbsListView mNewsListView;
    @InjectView(android.R.id.progress) ProgressBar mProgressBar;

    @Inject NewsItemCompactListCacheNew newsItemCompactListCache;

    private int mDisplayedViewId;
    private DTOCacheNew.Listener<NewsItemListKey, PaginatedDTO<NewsItemCompactDTO>> mFeaturedNewsListener;
    private FeaturedNewsHeadlineAdapter mFeaturedNewsAdapter;

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

        mFeaturedNewsAdapter = new FeaturedNewsHeadlineAdapter(getActivity(), R.layout.news_headline_item_view);
        mNewsListView.setAdapter(mFeaturedNewsAdapter);
    }

    @Override public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        DaggerUtils.inject(this);
    }

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override public void onResume()
    {
        super.onResume();
        if (mDisplayedViewId > 0)
        {
            mContentWrapper.setDisplayedChildByLayoutId(mDisplayedViewId);
        }

        detachFetchFeaturedNewsTask();
        NewsItemListFeaturedKey featuredNewsKey = new NewsItemListFeaturedKey(null, null);
        newsItemCompactListCache.register(featuredNewsKey, mFeaturedNewsListener);
        newsItemCompactListCache.getOrFetchAsync(featuredNewsKey);
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

        mFeaturedNewsAdapter.addAll(newsItemDTOKeys);
        mFeaturedNewsAdapter.notifyDataSetChanged();

        mContentWrapper.setDisplayedChildByLayoutId(mNewsListView.getId());
    }
}
