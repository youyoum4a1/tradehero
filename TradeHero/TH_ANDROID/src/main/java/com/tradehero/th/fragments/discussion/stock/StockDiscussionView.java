package com.tradehero.th.fragments.discussion.stock;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ProgressBar;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.tradehero.common.persistence.DTOCache;
import com.tradehero.common.widget.BetterViewAnimator;
import com.tradehero.th.R;
import com.tradehero.th.api.discussion.DiscussionKeyList;
import com.tradehero.th.api.discussion.DiscussionListKey;
import com.tradehero.th.api.discussion.PaginatedDiscussionListKey;
import com.tradehero.th.persistence.discussion.DiscussionListCache;
import javax.inject.Inject;
import timber.log.Timber;

/**
 * Created by thonguyen on 4/4/14.
 */
public class StockDiscussionView extends BetterViewAnimator
{
    @InjectView(android.R.id.list) AbsListView securityDiscussionList;
    @InjectView(android.R.id.empty) View emptyView;
    @InjectView(android.R.id.progress) ProgressBar progressBar;

    @Inject DiscussionListCache discussionListCache;

    private SecurityDiscussionAdapter securityDiscussionAdapter;
    private AbsListView.OnScrollListener securityDiscussionListScrollListener;
    private PaginatedDiscussionListKey paginatedSecurityDiscussionListKey;
    private DTOCache.GetOrFetchTask<DiscussionListKey, DiscussionKeyList> securityDiscussionFetchTask;
    private boolean loading;
    private DiscussionListKey discussionListKey;
    private int nextPageDelta;
    private SecurityDiscussionFetchListener securityDiscussionFetchListener;

    //<editor-fold desc="Constructors">
    public StockDiscussionView(Context context)
    {
        super(context);
    }

    public StockDiscussionView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();

        ButterKnife.inject(this);

        discussionListKey = new DiscussionListKey();
        securityDiscussionListScrollListener = new SecurityDiscussionListScrollListener();
        securityDiscussionFetchListener = new SecurityDiscussionFetchListener(true);
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();

        securityDiscussionAdapter = new SecurityDiscussionAdapter(
                getContext(),
                LayoutInflater.from(getContext()),
                R.layout.security_discussion_item);

        securityDiscussionList.setEmptyView(emptyView);
        securityDiscussionList.setAdapter(securityDiscussionAdapter);
        securityDiscussionList.setOnScrollListener(securityDiscussionListScrollListener);
    }

    @Override protected void onDetachedFromWindow()
    {
        super.onDetachedFromWindow();
    }

    private void fetchSecurityDiscussion()
    {
        detachSecurityDiscussionFetchTask();

        securityDiscussionFetchTask = discussionListCache.getOrFetch(paginatedSecurityDiscussionListKey, false, securityDiscussionFetchListener);
    }

    private void detachSecurityDiscussionFetchTask()
    {
        if (securityDiscussionFetchTask != null)
        {
            securityDiscussionFetchTask.setListener(null);
        }
        securityDiscussionFetchTask = null;
    }

    private class SecurityDiscussionListScrollListener implements AbsListView.OnScrollListener
    {
        @Override public void onScrollStateChanged(AbsListView absListView, int scrollState)
        {
            // do nothing for now
        }

        @Override public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
        {
            boolean shouldLoadMore =
                    Math.abs(totalItemCount - firstVisibleItem) <= visibleItemCount * calculateThreshold(totalItemCount, visibleItemCount);
            Timber.d("shouldLoadMore = %b, loading = %b", shouldLoadMore, loading);

            if (shouldLoadMore && !loading)
            {
                loading = true;
                if (paginatedSecurityDiscussionListKey == null)
                {
                    paginatedSecurityDiscussionListKey = new PaginatedDiscussionListKey(discussionListKey, 1);
                }

                if (nextPageDelta >= 0)
                {
                    paginatedSecurityDiscussionListKey = paginatedSecurityDiscussionListKey.next(nextPageDelta);

                    fetchSecurityDiscussion();
                }
            }
        }
    }


    private class SecurityDiscussionFetchListener implements DTOCache.Listener<DiscussionListKey,DiscussionKeyList>
    {
        private final boolean shouldAppend;

        public SecurityDiscussionFetchListener(boolean shouldAppend)
        {
            this.shouldAppend = shouldAppend;
        }

        @Override public void onDTOReceived(DiscussionListKey key, DiscussionKeyList value, boolean fromCache)
        {
            
        }

        @Override public void onErrorThrown(DiscussionListKey key, Throwable error)
        {

        }
    }

    private int calculateThreshold(int totalItemCount, int visibleItemCount)
    {
        return 0;
    }
}
