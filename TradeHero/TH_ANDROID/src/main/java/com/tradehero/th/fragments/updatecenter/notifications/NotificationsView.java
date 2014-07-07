package com.tradehero.th.fragments.updatecenter.notifications;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.common.utils.THToast;
import com.tradehero.common.widget.BetterViewAnimator;
import com.tradehero.th.R;
import com.tradehero.th.api.notification.NotificationDTO;
import com.tradehero.th.api.notification.NotificationKey;
import com.tradehero.th.api.notification.NotificationKeyList;
import com.tradehero.th.api.notification.NotificationListKey;
import com.tradehero.th.api.notification.PaginatedNotificationListKey;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.fragments.updatecenter.UpdateCenterFragment;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.network.retrofit.MiddleCallbackWeakList;
import com.tradehero.th.network.service.NotificationServiceWrapper;
import com.tradehero.th.persistence.notification.NotificationCache;
import com.tradehero.th.persistence.notification.NotificationListCache;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.utils.DaggerUtils;
import com.tradehero.th.utils.EndlessScrollingHelper;
import dagger.Lazy;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

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

    @NotNull private MiddleCallbackWeakList<Response> middleCallbacks;
    private DTOCacheNew.Listener<NotificationListKey, NotificationKeyList> notificationListFetchListener;
    private DTOCacheNew.Listener<NotificationListKey, NotificationKeyList> notificationListRefreshListener;
    private NotificationListKey notificationListKey;
    private NotificationListAdapter notificationListAdapter;
    private PullToRefreshBase.OnRefreshListener<ListView> notificationPullToRefreshListener;

    //<editor-fold desc="Constructors">
    public NotificationsView(Context context)
    {
        super(context);
        init();
    }

    public NotificationsView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    protected void init()
    {
        middleCallbacks = new MiddleCallbackWeakList<>();
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();

        ButterKnife.inject(this);
        DaggerUtils.inject(this);

        notificationListFetchListener = createNotificationFetchListener();
        notificationListRefreshListener = createNotificationRefreshListener();

        notificationListAdapter = createNotificationListAdapter();
    }

    private NotificationListAdapter createNotificationListAdapter()
    {
        return new NotificationListAdapter(
                getContext(),
                R.layout.notification_item_view);
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();

        if (notificationListFetchListener == null)
        {
            notificationListFetchListener = createNotificationFetchListener();
        }
        if (notificationListRefreshListener == null)
        {
            notificationListRefreshListener = createNotificationRefreshListener();
        }

        // for now, we have only one type of notification list
        notificationListKey = new NotificationListKey();

        notificationList.setAdapter(notificationListAdapter);
        notificationList.setOnItemClickListener(new NotificationListItemClickListener());
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

    private void unsetMiddleCallback()
    {
        middleCallbacks.detach();
    }

    @Override protected void onDetachedFromWindow()
    {
        detachNotificationListFetchTask();

        unsetMiddleCallback();

        notificationListFetchListener = null;
        notificationListRefreshListener = null;

        notificationListAdapter = null;
        notificationList.setAdapter(null);
        notificationList.setOnScrollListener(null);
        notificationList.setOnRefreshListener((PullToRefreshBase.OnRefreshListener) null);
        notificationList.setOnItemClickListener(null);

        ButterKnife.reset(this);
        super.onDetachedFromWindow();
    }

    private void fetchNextPageIfNecessary()
    {
        fetchNextPageIfNecessary(false);
    }

    public void fetchNextPageIfNecessary(boolean force)
    {
        detachNotificationListFetchTask();

        if (paginatedNotificationListKey == null)
        {
            paginatedNotificationListKey = new PaginatedNotificationListKey(notificationListKey, 1);
        }

        if (nextPageDelta >= 0)
        {
            paginatedNotificationListKey = paginatedNotificationListKey.next(nextPageDelta);
            notificationListCache.get().register(paginatedNotificationListKey, notificationListFetchListener);
            notificationListCache.get().getOrFetchAsync(paginatedNotificationListKey, force);
        }
    }

    private void resetPage()
    {
        if (notificationListKey == null)
        {
            notificationListKey = new NotificationListKey();
        }
        nextPageDelta = 0;
        paginatedNotificationListKey = new PaginatedNotificationListKey(notificationListKey, 1);
    }

    private void resetContent(NotificationKeyList notificationKeyList)
    {
        if (notificationListAdapter == null)
        {
            notificationListAdapter = createNotificationListAdapter();
        }
        notificationListAdapter.clear();
        notificationListAdapter.addAll(notificationKeyList);
        notificationListAdapter.notifyDataSetChanged();
        Timber.d("resetContent");
    }

    private void refreshCache(NotificationKeyList notificationKeyList)
    {
        notificationListCache.get().invalidateAll();
        PaginatedNotificationListKey firstPage = new PaginatedNotificationListKey(notificationListKey, 1);
        notificationListCache.get().put(firstPage, notificationKeyList);
    }

    private void refresh()
    {
        detachNotificationListRefreshTask();
        PaginatedNotificationListKey firstPage = new PaginatedNotificationListKey(notificationListKey, 1);
        notificationListCache.get().register(firstPage, notificationListRefreshListener);
        notificationListCache.get().getOrFetchAsync(firstPage, true);
        requestUpdateTabCounter();
    }

    private void detachNotificationListFetchTask()
    {
        notificationListCache.get().unregister(notificationListFetchListener);
    }

    private void detachNotificationListRefreshTask()
    {
        notificationListCache.get().unregister(notificationListRefreshListener);
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

    protected void reportNotificationRead(int pushId)
    {
        middleCallbacks.add(
                notificationServiceWrapper.markAsRead(
                        new NotificationKey(pushId),
                        createMarkNotificationAsReadCallback()));
    }

    private DTOCacheNew.Listener<NotificationListKey, NotificationKeyList> createNotificationFetchListener()
    {
        return new NotificationFetchListener(true);
    }

    private class NotificationFetchListener implements DTOCacheNew.Listener<NotificationListKey, NotificationKeyList>
    {
        private final boolean shouldAppend;

        public NotificationFetchListener(boolean shouldAppend)
        {
            this.shouldAppend = shouldAppend;
        }

        @Override public void onDTOReceived(NotificationListKey key, NotificationKeyList notificationKeyList)
        {
            onFinish();

            if (notificationKeyList != null)
            {
                //TODO right?
                nextPageDelta = notificationKeyList.isEmpty() ? -1 : 1;

                if (!shouldAppend)
                {
                    notificationListAdapter.clear();
                }
                notificationListAdapter.addAll(notificationKeyList);
                notificationListAdapter.notifyDataSetChanged();
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
                notificationListAdapter.clear();
                notificationList.onRefreshComplete();
            }

            setDisplayedChildByLayoutId(notificationList.getId());
        }
    }

    private DTOCacheNew.Listener<NotificationListKey, NotificationKeyList> createNotificationRefreshListener()
    {
        return new NotificationRefreshListener();
    }

    private class NotificationRefreshListener implements DTOCacheNew.Listener<NotificationListKey, NotificationKeyList>
    {
        @Override public void onDTOReceived(NotificationListKey key, NotificationKeyList notificationKeyList)
        {
            resetPage();
            resetContent(notificationKeyList);
            refreshCache(notificationKeyList);
            onFinish();
        }

        @Override public void onErrorThrown(NotificationListKey key, Throwable error)
        {
            onFinish();
            Timber.e("NotificationRefreshListener onErrorThrown");
            //THToast.show(new THException(error));
        }

        private void onFinish()
        {
            if (notificationList != null)
            {
                notificationList.onRefreshComplete();
            }
        }
    }

    protected Callback<Response> createMarkNotificationAsReadCallback()
    {
        return new NotificationMarkAsReadCallback();
    }

    protected class NotificationMarkAsReadCallback implements Callback<Response>
    {
        @Override public void success(Response response, Response response2)
        {
            if(notificationListAdapter!=null)
            {
                notificationListAdapter.notifyDataSetChanged();
            }
        }

        @Override public void failure(RetrofitError retrofitError)
        {
        }
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
            //nextPageDelta = 0;
            //paginatedNotificationListKey = null;

            //wrong usage
            //notificationList.setRefreshing();
            //fetchNextPageIfNecessary(true);
            refresh();
        }
    }

    private class NotificationListItemClickListener implements android.widget.AdapterView.OnItemClickListener
    {
        @Override public void onItemClick(AdapterView<?> parent, View itemView, int position, long id)
        {
            Object o = parent.getItemAtPosition(position);
            if (o instanceof NotificationKey)
            {
                NotificationDTO notificationDTO = notificationCache.get().get((NotificationKey) o);
                if (notificationDTO != null)
                {
                    NotificationClickHandler notificationClickHandler = new NotificationClickHandler(getContext(), notificationDTO);
                    notificationClickHandler.handleNotificationItemClicked();
                    reportNotificationRead(notificationDTO.pushId);
                }
            }
        }
    }
}
