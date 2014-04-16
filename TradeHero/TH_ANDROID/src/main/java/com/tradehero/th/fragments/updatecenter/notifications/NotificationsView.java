package com.tradehero.th.fragments.updatecenter.notifications;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.ProgressBar;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.tradehero.common.persistence.DTOCache;
import com.tradehero.common.utils.THToast;
import com.tradehero.common.widget.BetterViewAnimator;
import com.tradehero.th.R;
import com.tradehero.th.api.notification.NotificationDTO;
import com.tradehero.th.api.notification.NotificationKey;
import com.tradehero.th.api.notification.NotificationKeyList;
import com.tradehero.th.api.notification.NotificationListKey;
import com.tradehero.th.api.notification.PaginatedNotificationListKey;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.fragments.updatecenter.UpdateCenterFragment;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.network.retrofit.MiddleCallback;
import com.tradehero.th.network.service.NotificationServiceWrapper;
import com.tradehero.th.persistence.notification.NotificationCache;
import com.tradehero.th.persistence.notification.NotificationListCache;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.utils.DaggerUtils;
import com.tradehero.th.utils.EndlessScrollingHelper;
import dagger.Lazy;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

/**
 * Created by thonguyen on 3/4/14.
 */
public class NotificationsView extends BetterViewAnimator
{
    @InjectView(android.R.id.empty) View emptyView;
    @InjectView(android.R.id.progress) ProgressBar progressBar;
    @InjectView(R.id.notification_pull_to_refresh_list) PullToRefreshListView notificationList;

    @Inject Lazy<NotificationListCache> notificationListCache;
    @Inject Lazy<NotificationCache> notificationCache;
    @Inject NotificationServiceWrapper notificationServiceWrapper;
    @Inject UserProfileCache userProfileCache;
    @Inject CurrentUserId currentUserId;

    private PaginatedNotificationListKey paginatedNotificationListKey;
    private boolean loading;
    private int nextPageDelta;

    private Map<Integer, MiddleCallback<Response>> middleCallbackMap;
    private Map<Integer, Callback<Response>> callbackMap;

    private DTOCache.Listener<NotificationListKey, NotificationKeyList> notificationFetchListener;
    private DTOCache.GetOrFetchTask<NotificationListKey, NotificationKeyList> notificationFetchTask;
    private NotificationListKey notificationListKey;
    private NotificationListAdapter notificationListAdapter;
    private PullToRefreshBase.OnRefreshListener<ListView> notificationPullToRefreshListener;

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

        notificationListAdapter = createNotificationAdapterListener();
    }

    private NotificationListAdapter createNotificationAdapterListener()
    {
        return new NotificationListAdapter(
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

        initCallbackMap();

        // for now, we have only one type of notification list
        notificationListKey = new NotificationListKey();

        notificationList.setAdapter(notificationListAdapter);
        notificationList.setEmptyView(emptyView);

        // scroll event will activate fetch task automatically
        notificationList.setOnScrollListener(new NotificationListOnScrollListener());

        createOnRefreshListener();
        notificationList.setOnRefreshListener(notificationPullToRefreshListener);

        fetchNextPageIfNecessary();
    }

    private void createOnRefreshListener()
    {
        notificationPullToRefreshListener = new NotificationRefreshRequestListener();
    }

    private void initCallbackMap()
    {
        callbackMap = new HashMap<>();
        middleCallbackMap = new HashMap<>();
    }

    private void unsetMiddleCallback()
    {
        if (middleCallbackMap != null)
        {
            for (MiddleCallback<Response> middleCallback: middleCallbackMap.values())
            {
                middleCallback.setPrimaryCallback(null);
            }
            middleCallbackMap.clear();
        }

        if (callbackMap != null)
        {
            callbackMap.clear();
        }
    }

    @Override protected void onDetachedFromWindow()
    {
        detachNotificationFetchTask();

        unsetMiddleCallback();

        notificationFetchListener = null;

        notificationListAdapter = null;

        notificationList.setAdapter(null);

        notificationList.setOnScrollListener(null);

        notificationList.setOnRefreshListener((PullToRefreshBase.OnRefreshListener) null);

        ButterKnife.reset(this);
        super.onDetachedFromWindow();
    }

    private void fetchNextPageIfNecessary()
    {
        fetchNextPageIfNecessary(false);
    }

    private void fetchNextPageIfNecessary(boolean force)
    {
        detachNotificationFetchTask();

        if (paginatedNotificationListKey == null)
        {
            paginatedNotificationListKey = new PaginatedNotificationListKey(notificationListKey, 1);
        }

        if (nextPageDelta >= 0)
        {
            paginatedNotificationListKey = paginatedNotificationListKey.next(nextPageDelta);

            notificationFetchTask = notificationListCache.get().getOrFetch(paginatedNotificationListKey, force, notificationFetchListener);
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

            updateReadStatus(firstVisibleItem, visibleItemCount);
        }
    }

    private void updateReadStatus(int firstVisibleItem, int visibleItemCount)
    {
        int maxItemId = Math.min(firstVisibleItem + visibleItemCount, notificationListAdapter.getCount());
        for (int i = firstVisibleItem; i < maxItemId; ++i)
        {
            Object o = notificationListAdapter.getItem(i);
            if (o instanceof NotificationKey)
            {
                NotificationKey notificationKey = (NotificationKey) o;
                NotificationDTO notificationDTO = notificationCache.get().get(notificationKey);

                if (notificationDTO != null && notificationDTO.unread)
                {
                    reportNotificationRead(notificationDTO.pushId);
                }
            }
        }
    }

    private void reportNotificationRead(int pushId)
    {
        MiddleCallback<Response> middleCallback = middleCallbackMap.get(pushId);
        if (middleCallback == null)
        {
            middleCallback = notificationServiceWrapper.markAsRead(pushId, getCallback(pushId));
            middleCallbackMap.put(pushId, middleCallback);
        }
    }

    private Callback<Response> getCallback(int pushId)
    {
        Callback<Response> callback = callbackMap.get(pushId);
        if (callback == null)
        {
            callback = new NotificationMarkAsReadCallback(pushId);
        }
        return callback;
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

            // a bit hacky here to identify whether the list is forced to be refreshed
            Integer currentPage = paginatedNotificationListKey.getPage();
            if (currentPage != null && currentPage == 1)
            {
                notificationListAdapter.setItems(null);
                notificationList.onRefreshComplete();
            }

            setDisplayedChildByLayoutId(notificationList.getId());
        }
    }


    private class NotificationMarkAsReadCallback implements Callback<Response>
    {
        private final int pushId;

        public NotificationMarkAsReadCallback(int pushId)
        {
            this.pushId = pushId;
        }

        @Override public void success(Response response, Response response2)
        {
            if (response.getStatus() == 200)
            {
                Timber.d("Notification %d is reported as read");
                // TODO update title

                // mark it as read in the cache
                NotificationKey notificationKey = new NotificationKey(pushId);
                NotificationDTO notificationDTO = notificationCache.get().get(notificationKey);
                if (notificationDTO != null && notificationDTO.unread)
                {
                    notificationDTO.unread = false;
                    notificationCache.get().put(notificationKey, notificationDTO);
                    updateUnreadStatusInUserProfileCache();
                }
                middleCallbackMap.remove(pushId);
                callbackMap.remove(pushId);
            }
        }

        @Override public void failure(RetrofitError retrofitError)
        {
            Timber.d("Report failure for notification: %d", pushId);
        }
    }

    private void updateUnreadStatusInUserProfileCache()
    {
        // TODO synchronization problem
        UserBaseKey userBaseKey = currentUserId.toUserBaseKey();
        UserProfileDTO userProfileDTO = userProfileCache.get(currentUserId.toUserBaseKey());
        if (userProfileDTO.unreadNotificationsCount > 0)
        {
            --userProfileDTO.unreadNotificationsCount;
        }
        userProfileCache.put(userBaseKey, userProfileDTO);

        requestUpdateTabCounter();
    }

    private void requestUpdateTabCounter()
    {
        // TODO remove this hack after refactor messagecenterfragment
        Intent requestUpdateIntent = new Intent(UpdateCenterFragment.REQUEST_UPDATE_UNREAD_COUNTER);
        LocalBroadcastManager.getInstance(getContext()).sendBroadcast(requestUpdateIntent);
    }

    private class NotificationRefreshRequestListener implements PullToRefreshBase.OnRefreshListener<ListView>
    {
        @Override public void onRefresh(PullToRefreshBase<ListView> refreshView)
        {
            // reset initial data to get refresh list
            nextPageDelta = 0;
            paginatedNotificationListKey = null;

            notificationList.setRefreshing();
            fetchNextPageIfNecessary(true);
        }
    }
}
