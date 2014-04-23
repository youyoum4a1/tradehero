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
import com.tradehero.common.utils.THToast;
import com.tradehero.common.widget.BetterViewAnimator;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.discussion.DiscussionKeyList;
import com.tradehero.th.api.discussion.DiscussionType;
import com.tradehero.th.api.discussion.key.DiscussionListKey;
import com.tradehero.th.api.discussion.key.PaginatedDiscussionListKey;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.persistence.discussion.DiscussionListCache;
import com.tradehero.th.persistence.security.SecurityCompactCache;
import com.tradehero.th.utils.DaggerUtils;
import com.tradehero.th.utils.EndlessScrollingHelper;
import javax.inject.Inject;

/**
 * Created by thonguyen on 4/4/14.
 */
public class SecurityDiscussionView extends BetterViewAnimator
    implements DTOView<SecurityId>
{
    @InjectView(android.R.id.list) AbsListView securityDiscussionList;
    @InjectView(android.R.id.empty) View emptyView;
    @InjectView(android.R.id.progress) ProgressBar progressBar;

    @Inject SecurityCompactCache securityCompactCache;
    @Inject DiscussionListCache discussionListCache;

    private SecurityDiscussionAdapter securityDiscussionAdapter;
    private AbsListView.OnScrollListener securityDiscussionListScrollListener;
    private PaginatedDiscussionListKey paginatedSecurityDiscussionListKey;
    private DTOCache.GetOrFetchTask<DiscussionListKey, DiscussionKeyList> securityDiscussionFetchTask;
    private DiscussionListKey discussionListKey;
    private SecurityDiscussionFetchListener securityDiscussionFetchListener;

    private boolean loading;
    private int nextPageDelta;
    private DTOCache.GetOrFetchTask<SecurityId, SecurityCompactDTO> securityCompactCacheFetchTask;
    private DTOCache.Listener<SecurityId, SecurityCompactDTO> securityCompactCacheListener;

    //<editor-fold desc="Constructors">
    public SecurityDiscussionView(Context context)
    {
        super(context);
    }

    public SecurityDiscussionView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();

        ButterKnife.inject(this);
        DaggerUtils.inject(this);

        securityCompactCacheListener = new SecurityCompactCacheListener();
        securityDiscussionFetchListener = new SecurityDiscussionFetchListener(false);
        securityDiscussionListScrollListener = new SecurityDiscussionListScrollListener();

        securityDiscussionAdapter = new SecurityDiscussionAdapter(
                getContext(),
                LayoutInflater.from(getContext()),
                R.layout.security_discussion_item_view);
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();

        securityDiscussionList.setEmptyView(emptyView);
        securityDiscussionList.setAdapter(securityDiscussionAdapter);
        securityDiscussionList.setOnScrollListener(securityDiscussionListScrollListener);

        setDisplayedChildByLayoutId(progressBar.getId());
    }

    @Override protected void onDetachedFromWindow()
    {
        detachSecurityCompactCacheTask();
        detachSecurityDiscussionFetchTask();

        super.onDetachedFromWindow();
    }

    private void fetchNextPageIfNecessary(boolean force)
    {
        detachSecurityDiscussionFetchTask();

        if (paginatedSecurityDiscussionListKey == null)
        {
            paginatedSecurityDiscussionListKey = new PaginatedDiscussionListKey(discussionListKey, 1);
        }

        if (nextPageDelta >= 0)
        {
            paginatedSecurityDiscussionListKey = paginatedSecurityDiscussionListKey.next(nextPageDelta);

            securityDiscussionFetchTask = discussionListCache.getOrFetch(paginatedSecurityDiscussionListKey, force, securityDiscussionFetchListener);
            securityDiscussionFetchTask.execute();
        }
    }

    private void detachSecurityDiscussionFetchTask()
    {
        if (securityDiscussionFetchTask != null)
        {
            securityDiscussionFetchTask.setListener(null);
        }
        securityDiscussionFetchTask = null;
    }

    @Override public void display(SecurityId securityId)
    {
        detachSecurityCompactCacheTask();
        securityCompactCacheFetchTask = securityCompactCache.getOrFetch(securityId, true, securityCompactCacheListener);
        securityCompactCacheFetchTask.execute();
    }

    private void detachSecurityCompactCacheTask()
    {
        if (securityCompactCacheFetchTask != null)
        {
            securityCompactCacheFetchTask.setListener(null);
        }
        securityCompactCacheFetchTask = null;
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
                    Math.abs(totalItemCount - firstVisibleItem) <= EndlessScrollingHelper.calculateThreshold(totalItemCount, visibleItemCount);

            if (discussionListKey != null && shouldLoadMore && !loading)
            {
                loading = true;

                fetchNextPageIfNecessary(true);
            }
        }
    }

    private class SecurityDiscussionFetchListener implements DTOCache.Listener<DiscussionListKey,DiscussionKeyList>
    {
        private boolean shouldAppend;

        public SecurityDiscussionFetchListener(boolean shouldAppend)
        {
            this.shouldAppend = shouldAppend;
        }

        @Override public void onDTOReceived(DiscussionListKey key, DiscussionKeyList discussionKeyList, boolean fromCache)
        {
            if (fromCache)
            {
                return;
            }

            onFinish();

            if (discussionKeyList != null)
            {
                nextPageDelta = discussionKeyList.isEmpty() ? -1 : 1;

                if (shouldAppend)
                {
                    securityDiscussionAdapter.appendMore(discussionKeyList);
                }
                else
                {
                    securityDiscussionAdapter.setItems(discussionKeyList);
                    securityDiscussionAdapter.notifyDataSetChanged();
                    shouldAppend = true;
                }
            }
        }

        @Override public void onErrorThrown(DiscussionListKey key, Throwable error)
        {
            onFinish();

            nextPageDelta = 0;

            THToast.show(new THException(error));
        }

        private void onFinish()
        {
            loading = false;

            setDisplayedChildByLayoutId(securityDiscussionList.getId());
        }
    }

    private class SecurityCompactCacheListener implements DTOCache.Listener<SecurityId,SecurityCompactDTO>
    {
        @Override public void onDTOReceived(SecurityId key, SecurityCompactDTO securityCompactDTO, boolean fromCache)
        {
            discussionListKey = new DiscussionListKey(DiscussionType.SECURITY, securityCompactDTO.id);

            nextPageDelta = 0;
            fetchNextPageIfNecessary(true);
        }

        @Override public void onErrorThrown(SecurityId key, Throwable error)
        {
            THToast.show(new THException(error));
        }
    }
}
