package com.tradehero.th.fragments.discussion.stock;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ProgressBar;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.common.utils.THToast;
import com.tradehero.common.widget.BetterViewAnimator;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.discussion.DiscussionKeyList;
import com.tradehero.th.api.discussion.DiscussionType;
import com.tradehero.th.api.discussion.key.DiscussionKey;
import com.tradehero.th.api.discussion.key.DiscussionListKey;
import com.tradehero.th.api.discussion.key.PaginatedDiscussionListKey;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.fragments.discussion.DiscussionSetAdapter;
import com.tradehero.th.fragments.discussion.SingleViewDiscussionSetAdapter;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.persistence.discussion.DiscussionListCacheNew;
import com.tradehero.th.persistence.security.SecurityCompactCache;
import com.tradehero.th.utils.DaggerUtils;
import com.tradehero.th.utils.EndlessScrollingHelper;
import java.util.Collection;
import javax.inject.Inject;
import timber.log.Timber;

public class SecurityDiscussionView extends BetterViewAnimator
    implements DTOView<SecurityId>, DiscussionListCacheNew.DiscussionKeyListListener
{
    @InjectView(android.R.id.list) AbsListView securityDiscussionList;
    @InjectView(android.R.id.empty) View emptyView;
    @InjectView(android.R.id.progress) ProgressBar progressBar;

    @Inject SecurityCompactCache securityCompactCache;
    @Inject DiscussionListCacheNew discussionListCache;

    private DiscussionSetAdapter securityDiscussionAdapter;
    private AbsListView.OnScrollListener securityDiscussionListScrollListener;
    private PaginatedDiscussionListKey paginatedSecurityDiscussionListKey;
    private DiscussionListKey discussionListKey;

    private boolean loading;
    private int nextPageDelta;
    private DTOCacheNew.Listener<SecurityId, SecurityCompactDTO> securityCompactCacheFetchListener;

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

        securityDiscussionListScrollListener = new SecurityDiscussionListScrollListener();
        securityCompactCacheFetchListener = createSecurityCompactCacheListener();
        securityDiscussionAdapter = createDiscussionAdapter(null);
    }

    protected DiscussionSetAdapter createDiscussionAdapter(Collection<DiscussionKey> initial)
    {
        return new SingleViewDiscussionSetAdapter(
                getContext(),
                initial,
                R.layout.security_discussion_item_view);
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();

        ButterKnife.inject(this);
        if (securityCompactCacheFetchListener == null)
        {
            securityCompactCacheFetchListener = createSecurityCompactCacheListener();
        }
        securityDiscussionList.setEmptyView(emptyView);
        securityDiscussionList.setAdapter(securityDiscussionAdapter);
        securityDiscussionList.setOnScrollListener(securityDiscussionListScrollListener);

        setDisplayedChildByLayoutId(progressBar.getId());
    }

    @Override protected void onDetachedFromWindow()
    {
        detachSecurityCompactCacheTask();
        securityCompactCacheFetchListener = null;
        discussionListCache.unregister(this);
        ButterKnife.reset(this);

        super.onDetachedFromWindow();
    }

    private void fetchNextPageIfNecessary(boolean force)
    {
        if (paginatedSecurityDiscussionListKey != null)
        {
            discussionListCache.unregister(paginatedSecurityDiscussionListKey, this);
        }

        if (paginatedSecurityDiscussionListKey == null)
        {
            paginatedSecurityDiscussionListKey = new PaginatedDiscussionListKey(discussionListKey, 1);
        }

        if (nextPageDelta >= 0)
        {
            paginatedSecurityDiscussionListKey = paginatedSecurityDiscussionListKey.next(nextPageDelta);

            discussionListCache.register(paginatedSecurityDiscussionListKey, this);
            discussionListCache.getOrFetchAsync(paginatedSecurityDiscussionListKey, force);
        }
    }

    @Override public void display(SecurityId securityId)
    {
        detachSecurityCompactCacheTask();
        securityCompactCache.register(securityId, securityCompactCacheFetchListener);
        securityCompactCache.getOrFetchAsync(securityId, false);
    }

    private void detachSecurityCompactCacheTask()
    {
        securityCompactCache.unregister(securityCompactCacheFetchListener);
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

    private boolean shouldAppend = false;

    @Override public void onDTOReceived(DiscussionListKey key, DiscussionKeyList discussionKeyList)
    {
        onFinish();

        if (discussionKeyList != null)
        {
            nextPageDelta = discussionKeyList.isEmpty() ? -1 : 1;

            Timber.d("nextPageDelta: %d, page: %d, received: %d", nextPageDelta, paginatedSecurityDiscussionListKey.page, discussionKeyList.size());

            if (shouldAppend)
            {
                securityDiscussionAdapter.appendTail(discussionKeyList);
                securityDiscussionAdapter.notifyDataSetChanged();
            }
            else
            {
                securityDiscussionAdapter = createDiscussionAdapter(discussionKeyList);
                securityDiscussionList.setAdapter(securityDiscussionAdapter);
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

    private DTOCacheNew.Listener<SecurityId, SecurityCompactDTO> createSecurityCompactCacheListener()
    {
        return new SecurityCompactCacheListener();
    }

    private class SecurityCompactCacheListener implements DTOCacheNew.Listener<SecurityId, SecurityCompactDTO>
    {
        @Override public void onDTOReceived(SecurityId key, SecurityCompactDTO securityCompactDTO)
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
