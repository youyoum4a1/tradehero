package com.tradehero.th.fragments.updatecenter.notifications;

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
import com.tradehero.th.api.notification.NotificationKeyList;
import com.tradehero.th.api.notification.NotificationListKey;
import com.tradehero.th.api.notification.PaginatedNotificationListKey;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.persistence.notification.NotificationListCache;
import com.tradehero.th.utils.DaggerUtils;
import com.tradehero.th.utils.EndlessScrollingHelper;
import dagger.Lazy;
import javax.inject.Inject;

/**
 * Created by thonguyen on 3/4/14.
 */
public class NotificationsView extends BetterViewAnimator
{
    @InjectView(android.R.id.empty) View emptyView;
    @InjectView(android.R.id.progress) ProgressBar progressBar;
    @InjectView(android.R.id.list) AbsListView notificationList;

    @Inject Lazy<NotificationListCache> notificationListCache;

    private PaginatedNotificationListKey paginatedNotificationListKey;
    private boolean loading;
    private int nextPageDelta;

    private DTOCache.Listener<NotificationListKey, NotificationKeyList> notificationFetchListener;
    private DTOCache.GetOrFetchTask<NotificationListKey, NotificationKeyList> notificationFetchTask;
    private NotificationListKey notificationListKey;
    private NotificationListAdapter notificationListAdapter;

    //<editor-fold desc="Constructors">
    public NotificationsView(Context context)
    {
        super(context);
    }

    public NotificationsView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();

        ButterKnife.inject(this);
        DaggerUtils.inject(this);

        createNotificationFetchListener();

        createNotificationAdapterListener();
    }

    private void createNotificationAdapterListener()
    {
        notificationListAdapter = new NotificationListAdapter(
                getContext(),
                LayoutInflater.from(getContext()),
                R.layout.notification_item_view);
    }

    private void createNotificationFetchListener()
    {
        notificationFetchListener = new NotificationFetchListener(true);
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();

        createNotificationFetchListener();

        createNotificationAdapterListener();

        // for now, we have only one type of notification list
        notificationListKey = new NotificationListKey();

        notificationList.setAdapter(notificationListAdapter);
        notificationList.setEmptyView(emptyView);

        // scroll event will activate fetch task automatically
        notificationList.setOnScrollListener(new NotificationListOnScrollListener());
    }

    @Override protected void onDetachedFromWindow()
    {
        detachNotificationFetchTask();

        notificationFetchListener = null;

        notificationListAdapter = null;

        notificationList.setAdapter(null);

        notificationList.setOnScrollListener(null);

        ButterKnife.reset(this);
        super.onDetachedFromWindow();
    }

    private void fetchNextPageIfNecessary()
    {
        detachNotificationFetchTask();

        if (paginatedNotificationListKey == null)
        {
            paginatedNotificationListKey = new PaginatedNotificationListKey(notificationListKey, 1);
        }

        if (nextPageDelta >= 0)
        {
            paginatedNotificationListKey = paginatedNotificationListKey.next(nextPageDelta);

            notificationFetchTask = notificationListCache.get().getOrFetch(paginatedNotificationListKey, false, notificationFetchListener);
            notificationFetchTask.execute();
        }
    }

    private void detachNotificationFetchTask()
    {
        if (notificationFetchTask != null)
        {
            notificationFetchTask.setListener(null);
        }
        notificationFetchTask = null;
    }

    private class NotificationListOnScrollListener implements AbsListView.OnScrollListener
    {
        @Override public void onScrollStateChanged(AbsListView absListView, int scrollState)
        {
            // nothing for now
        }

        @Override public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
        {
            boolean shouldLoadMore =
                    Math.abs(totalItemCount - firstVisibleItem) <= EndlessScrollingHelper.calculateThreshold(totalItemCount, visibleItemCount);

            if (shouldLoadMore && !loading)
            {
                loading = true;

                fetchNextPageIfNecessary();
            }
        }
    }

    private class NotificationFetchListener implements DTOCache.Listener<NotificationListKey, NotificationKeyList>
    {
        private final boolean shouldAppend;

        public NotificationFetchListener(boolean shouldAppend)
        {
            this.shouldAppend = shouldAppend;
        }

        @Override public void onDTOReceived(NotificationListKey key, NotificationKeyList notificationKeyList, boolean fromCache)
        {
            onFinish();

            if (notificationKeyList != null)
            {
                nextPageDelta = notificationKeyList.isEmpty() ? -1 : 1;

                if (shouldAppend)
                {
                    notificationListAdapter.appendMore(notificationKeyList);
                }
                else
                {
                    notificationListAdapter.setItems(notificationKeyList);
                    notificationListAdapter.notifyDataSetChanged();
                }
            }
        }

        @Override public void onErrorThrown(NotificationListKey key, Throwable error)
        {
            onFinish();

            nextPageDelta = 0;
            THToast.show(new THException(error));
        }

        private void onFinish()
        {
            loading = false;

            setDisplayedChildByLayoutId(notificationList.getId());
        }
    }
}
