package com.tradehero.th.fragments.updatecenter.notifications;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.tradehero.common.utils.THToast;
import com.tradehero.common.widget.BetterViewAnimator;
import com.tradehero.th.BottomTabs;
import com.tradehero.th.BottomTabsQuickReturnListViewListener;
import com.tradehero.th.R;
import com.tradehero.th.api.BaseResponseDTO;
import com.tradehero.th.api.notification.NotificationDTO;
import com.tradehero.th.api.notification.NotificationKey;
import com.tradehero.th.api.notification.NotificationListKey;
import com.tradehero.th.api.notification.PaginatedNotificationDTO;
import com.tradehero.th.api.notification.PaginatedNotificationListKey;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.fragments.DashboardTabHost;
import com.tradehero.th.fragments.updatecenter.UpdateCenterFragment;
import com.tradehero.th.inject.HierarchyInjector;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.network.service.NotificationServiceWrapper;
import com.tradehero.th.persistence.notification.NotificationListCacheRx;
import com.tradehero.th.utils.EndlessScrollingHelper;
import com.tradehero.th.widget.MultiScrollListener;
import dagger.Lazy;
import javax.inject.Inject;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.internal.util.SubscriptionList;
import rx.observers.EmptyObserver;
import timber.log.Timber;

public class NotificationsView extends BetterViewAnimator
{
    @InjectView(android.R.id.empty) View emptyView;
    @InjectView(R.id.notification_pull_to_refresh_list) AbsListView notificationList;
    @InjectView(R.id.swipe_container) SwipeRefreshLayout swipeRefreshLayout;
    @InjectView(R.id.readAllLayout) View readAllLayout;

    @Inject Lazy<NotificationListCacheRx> notificationListCache;
    @Inject NotificationServiceWrapper notificationServiceWrapper;
    @Inject CurrentUserId currentUserId;
    @Inject @BottomTabsQuickReturnListViewListener AbsListView.OnScrollListener dashboardBottomTabsListViewScrollListener;
    @Inject @BottomTabs Lazy<DashboardTabHost> dashboardTabHost;

    private PaginatedNotificationListKey paginatedNotificationListKey;
    private boolean loading;
    private int nextPageDelta;

    @NonNull private SubscriptionList subscriptionList;
    private NotificationListKey notificationListKey;
    private NotificationListAdapter notificationListAdapter;
    private SwipeRefreshLayout.OnRefreshListener notificationRefreshListener;

    //<editor-fold desc="Constructors">
    @SuppressWarnings("UnusedDeclaration")
    public NotificationsView(Context context)
    {
        super(context);
        init();
    }

    @SuppressWarnings("UnusedDeclaration")
    public NotificationsView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    protected void init()
    {
        subscriptionList = new SubscriptionList();
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();

        ButterKnife.inject(this);
        HierarchyInjector.inject(this);

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

        // for now, we have only one type of notification list
        notificationListKey = new NotificationListKey();

        notificationList.setAdapter(notificationListAdapter);
        notificationList.setOnItemClickListener(new NotificationListItemClickListener());
        notificationList.setEmptyView(emptyView);

        // scroll event will activate fetch task automatically
        notificationList.setOnScrollListener(
                new MultiScrollListener(new NotificationListOnScrollListener(), dashboardBottomTabsListViewScrollListener));

        createOnRefreshListener();
        swipeRefreshLayout.setOnRefreshListener(notificationRefreshListener);

        fetchNextPageIfNecessary();

        if (readAllLayout != null)
        {
            readAllLayout.setTranslationY(dashboardTabHost.get().getTranslationY());
        }
        dashboardTabHost.get().setOnTranslate((x, y) -> readAllLayout.setTranslationY(y));
    }

    @OnClick(R.id.readAllLayout)
    protected void onReadAllLayoutClicked()
    {
        reportNotificationReadAll();
    }

    private void createOnRefreshListener()
    {
        notificationRefreshListener = new NotificationRefreshRequestListener();
    }

    @Override protected void onDetachedFromWindow()
    {
        subscriptionList.unsubscribe();

        notificationListAdapter = null;
        notificationList.setAdapter(null);
        notificationList.setOnScrollListener(null);
        swipeRefreshLayout.setOnRefreshListener(null);
        notificationList.setOnItemClickListener(null);

        dashboardTabHost.get().setOnTranslate(null);

        ButterKnife.reset(this);
        super.onDetachedFromWindow();
    }

    private void fetchNextPageIfNecessary()
    {
        fetchNextPageIfNecessary(false);
    }

    public void fetchNextPageIfNecessary(boolean force)
    {
        if (paginatedNotificationListKey == null)
        {
            paginatedNotificationListKey = new PaginatedNotificationListKey(notificationListKey, 1);
        }

        if (nextPageDelta >= 0)
        {
            paginatedNotificationListKey = paginatedNotificationListKey.next(nextPageDelta);
            subscriptionList.add(notificationListCache.get()
                    .get(paginatedNotificationListKey)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(createNotificationFetchObserver()));
        }
    }

    private void refresh()
    {
        PaginatedNotificationListKey firstPage = new PaginatedNotificationListKey(notificationListKey, 1);
        subscriptionList.add(notificationListCache.get()
                .get(firstPage)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createNotificationFetchObserver()));
        requestUpdateTabCounter();
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
        subscriptionList.add(
                notificationServiceWrapper.markAsReadRx(
                        currentUserId.toUserBaseKey(),
                        new NotificationKey(pushId))
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(createMarkNotificationAsReadObserver()));
    }

    protected void reportNotificationReadAll()
    {
        subscriptionList.add(
                notificationServiceWrapper.markAsReadAllRx(
                        currentUserId.toUserBaseKey())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(createMarkNotificationAsReadAllObserver()));
    }

    protected Observer<Pair<NotificationListKey, PaginatedNotificationDTO>> createNotificationFetchObserver()
    {
        return new NotificationFetchObserver();
    }

    private class NotificationFetchObserver implements Observer<Pair<NotificationListKey, PaginatedNotificationDTO>>
    {
        @Override public void onNext(Pair<NotificationListKey, PaginatedNotificationDTO> pair)
        {
            onFinish();

            if (pair.second != null)
            {
                //TODO right?
                nextPageDelta = pair.second.getData().isEmpty() ? -1 : 1;

                notificationListAdapter.addAll(pair.second.getData());
                notificationListAdapter.notifyDataSetChanged();
                setReadAllLayoutVisible();
            }
        }

        @Override public void onCompleted()
        {
        }

        @Override public void onError(Throwable e)
        {
            onFinish();

            nextPageDelta = 0;
            THToast.show(new THException(e));
        }

        private void onFinish()
        {
            loading = false;
            swipeRefreshLayout.setRefreshing(false);

            // a bit hacky here to identify whether the list is forced to be refreshed
            Integer currentPage = paginatedNotificationListKey.getPage();
            if (currentPage != null && currentPage == 1)
            {
                notificationListAdapter.clear();
            }
            setNotificationListShow();
        }
    }

    private void setNotificationListShow()
    {
        setDisplayedChildByLayoutId(R.id.listViewLayout);
    }

    protected Observer<BaseResponseDTO> createMarkNotificationAsReadObserver()
    {
        return new NotificationMarkAsReadObserver();
    }

    protected class NotificationMarkAsReadObserver extends EmptyObserver<BaseResponseDTO>
    {
        @Override public void onNext(BaseResponseDTO baseResponseDTO)
        {
            if (notificationListAdapter != null)
            {
                notificationListAdapter.notifyDataSetChanged();
            }
            requestUpdateTabCounter();
        }
    }

    protected Observer<BaseResponseDTO> createMarkNotificationAsReadAllObserver()
    {
        return new NotificationMarkAsReadAllObserver();
    }

    protected class NotificationMarkAsReadAllObserver extends EmptyObserver<BaseResponseDTO>
    {
        @Override public void onNext(BaseResponseDTO baseResponseDTO)
        {
            Timber.d("NotificationMarkAsReadAllCallback success");
            setAllNotificationRead();
            setReadAllLayoutVisible();
            requestUpdateTabCounter();
        }
    }

    private void requestUpdateTabCounter()
    {
        // TODO remove this hack after refactor messagecenterfragment
        Intent requestUpdateIntent = new Intent(UpdateCenterFragment.REQUEST_UPDATE_UNREAD_COUNTER);
        LocalBroadcastManager.getInstance(getContext()).sendBroadcast(requestUpdateIntent);
    }

    private class NotificationRefreshRequestListener implements SwipeRefreshLayout.OnRefreshListener
    {
        @Override public void onRefresh()
        {
            refresh();
        }
    }

    private class NotificationListItemClickListener implements android.widget.AdapterView.OnItemClickListener
    {
        @Override public void onItemClick(AdapterView<?> parent, View itemView, int position, long id)
        {
            NotificationDTO notificationDTO = (NotificationDTO) parent.getItemAtPosition(position);
            NotificationClickHandler notificationClickHandler = new NotificationClickHandler(getContext(), notificationDTO);
            notificationClickHandler.handleNotificationItemClicked();
            reportNotificationRead(notificationDTO.pushId);
        }
    }

    private void setReadAllLayoutVisible()
    {
        boolean haveUnread = true;
        int itemCount = notificationListAdapter.getCount();
        for (int i = 0; i < itemCount; i++)
        {
            if (notificationListAdapter.getItem(i).unread)
            {
                haveUnread = true;
                break;
            }
        }

        if (readAllLayout != null)
        {
            readAllLayout.setVisibility(haveUnread ? View.VISIBLE : View.GONE);
        }
    }

    private void setAllNotificationRead()
    {
        int itemCount = notificationListAdapter.getCount();
        for (int i = 0; i < itemCount; i++)
        {
            notificationListAdapter.getItem(i).unread = false;
        }
        notificationListAdapter.notifyDataSetChanged();
    }
}
