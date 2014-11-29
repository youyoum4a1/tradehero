package com.tradehero.th.fragments.discussion.stock;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ProgressBar;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.tradehero.common.utils.THToast;
import com.tradehero.common.widget.BetterViewAnimator;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.discussion.AbstractDiscussionDTO;
import com.tradehero.th.api.discussion.DiscussionDTO;
import com.tradehero.th.api.discussion.DiscussionKeyList;
import com.tradehero.th.api.discussion.DiscussionType;
import com.tradehero.th.api.discussion.key.DiscussionKey;
import com.tradehero.th.api.discussion.key.DiscussionListKey;
import com.tradehero.th.api.discussion.key.PaginatedDiscussionListKey;
import com.tradehero.th.api.pagination.PaginatedDTO;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.fragments.discussion.DiscussionSetAdapter;
import com.tradehero.th.fragments.discussion.SingleViewDiscussionSetAdapter;
import com.tradehero.th.inject.HierarchyInjector;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.persistence.discussion.DiscussionListCacheRx;
import com.tradehero.th.persistence.security.SecurityCompactCacheRx;
import com.tradehero.th.utils.EndlessScrollingHelper;
import com.tradehero.th.widget.MultiScrollListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.inject.Inject;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

public class SecurityDiscussionView extends BetterViewAnimator
        implements DTOView<SecurityId>
{
    @InjectView(android.R.id.list) AbsListView securityDiscussionList;
    @InjectView(android.R.id.empty) View emptyView;
    @InjectView(android.R.id.progress) ProgressBar progressBar;

    @Inject SecurityCompactCacheRx securityCompactCache;
    @Nullable private Subscription securityCompactCacheSubscription;
    @Inject DiscussionListCacheRx discussionListCache;

    @NonNull private List<Subscription> discussionListCacheSubscriptions;
    private DiscussionSetAdapter securityDiscussionAdapter;
    private AbsListView.OnScrollListener securityDiscussionListScrollListener;
    private PaginatedDiscussionListKey paginatedSecurityDiscussionListKey;
    private DiscussionListKey discussionListKey;

    private boolean loading;
    private int nextPageDelta;

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

        discussionListCacheSubscriptions = new ArrayList<>();
        ButterKnife.inject(this);
        HierarchyInjector.inject(this);

        securityDiscussionListScrollListener = new SecurityDiscussionListScrollListener();
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
        securityDiscussionList.setEmptyView(emptyView);
        securityDiscussionList.setAdapter(securityDiscussionAdapter);

        setDisplayedChildByLayoutId(progressBar.getId());
    }

    public void setScrollListener(AbsListView.OnScrollListener onScrollListener)
    {
        securityDiscussionList.setOnScrollListener(
                new MultiScrollListener(securityDiscussionListScrollListener, onScrollListener));
    }

    public void removeScrollListener()
    {
        securityDiscussionList.setOnScrollListener(null);
    }

    @Override protected void onDetachedFromWindow()
    {
        detachSecurityCompactCacheSubscription();
        securityCompactCacheSubscription = null;
        detachDiscussionListCache();
        ButterKnife.reset(this);

        super.onDetachedFromWindow();
    }

    protected void detachDiscussionListCache()
    {
        for (Subscription subscription : discussionListCacheSubscriptions)
        {
            subscription.unsubscribe();
        }
        discussionListCacheSubscriptions.clear();
    }

    private void fetchNextPageIfNecessary()
    {
        if (paginatedSecurityDiscussionListKey == null)
        {
            paginatedSecurityDiscussionListKey = new PaginatedDiscussionListKey(discussionListKey, 1);
        }

        if (nextPageDelta >= 0)
        {
            paginatedSecurityDiscussionListKey = paginatedSecurityDiscussionListKey.next(nextPageDelta);

            discussionListCacheSubscriptions.add(discussionListCache.get(paginatedSecurityDiscussionListKey)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(createDiscussionListObserver()));
        }
    }

    @Override public void display(SecurityId securityId)
    {
        detachSecurityCompactCacheSubscription();
        securityCompactCacheSubscription = securityCompactCache.get(securityId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Pair<SecurityId, SecurityCompactDTO>>()
                {
                    @Override public void onCompleted()
                    {
                    }

                    @Override public void onError(Throwable e)
                    {
                        THToast.show(new THException(e));
                    }

                    @Override public void onNext(Pair<SecurityId, SecurityCompactDTO> pair)
                    {
                        discussionListKey = new DiscussionListKey(DiscussionType.SECURITY, pair.second.id);
                        nextPageDelta = 0;
                        fetchNextPageIfNecessary();
                    }
                });
    }

    private void detachSecurityCompactCacheSubscription()
    {
        Subscription subscriptionCopy = securityCompactCacheSubscription;
        if (subscriptionCopy != null)
        {
            subscriptionCopy.unsubscribe();
        }
        securityCompactCacheSubscription = null;
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

                fetchNextPageIfNecessary();
            }
        }
    }

    private boolean shouldAppend = false;

    protected Observer<Pair<DiscussionListKey, PaginatedDTO<DiscussionDTO>>> createDiscussionListObserver()
    {
        return new Observer<Pair<DiscussionListKey, PaginatedDTO<DiscussionDTO>>>()
        {
            @Override public void onCompleted()
            {
            }

            @Override public void onError(Throwable e)
            {
                onFinish();
                nextPageDelta = 0;
                THToast.show(new THException(e));
            }

            @Override public void onNext(Pair<DiscussionListKey, PaginatedDTO<DiscussionDTO>> pair)
            {
                onFinish();

                List<DiscussionDTO> list = pair.second.getData();
                if (list != null)
                {
                    DiscussionKeyList discussionKeyList = new DiscussionKeyList();
                    for (AbstractDiscussionDTO abstractDiscussionDTO : list)
                    {
                        discussionKeyList.add(abstractDiscussionDTO.getDiscussionKey());
                    }

                    nextPageDelta = discussionKeyList.isEmpty() ? -1 : 1;

                    Timber.d("nextPageDelta: %d, page: %d, received: %d", nextPageDelta, paginatedSecurityDiscussionListKey.page,
                            discussionKeyList.size());

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
        };
    }

    private void onFinish()
    {
        loading = false;

        if (securityDiscussionList != null)
        {
            setDisplayedChildByLayoutId(securityDiscussionList.getId());
        }
    }
}
