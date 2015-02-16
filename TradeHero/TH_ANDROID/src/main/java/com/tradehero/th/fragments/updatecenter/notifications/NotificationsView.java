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
import com.tradehero.th.adapters.ArrayDTOAdapterNew;
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
import com.tradehero.th.rx.ToastOnErrorAction;
import com.tradehero.th.utils.EndlessScrollingHelper;
import com.tradehero.th.widget.MultiScrollListener;
import dagger.Lazy;
import javax.inject.Inject;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.internal.util.SubscriptionList;

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
    private ArrayDTOAdapterNew<NotificationDTO, NotificationItemView> notificationListAdapter;
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

    private ArrayDTOAdapterNew<NotificationDTO, NotificationItemView> createNotificationListAdapter()
    {
        return new ArrayDTOAdapterNew<>(
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
        dashboardTabHost.get().setOnTranslate(new DashboardTabHost.OnTranslateListener()
        {
            @Override public void onTranslate(float x, float y)
            {
                readAllLayout.setTranslationY(y);
            }
        });
    }

    @SuppressWarnings("UnusedDeclaration")
    @OnClick(R.id.readAllLayout)
    protected void onReadAllLayoutClicked(View view)
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
        if (paginatedNotificationListKey == null)
        {
            paginatedNotificationListKey = new PaginatedNotificationListKey(notificationListKey, 1);
        }

        if (nextPageDelta >= 0)
        {
            paginatedNotificationListKey = paginatedNotificationListKey.next(nextPageDelta);
            nextPageDelta = -1;
            subscriptionList.add(notificationListCache.get()
                    .get(paginatedNotificationListKey)
                    .observeOn(AndroidSchedulers.mainThread())
                    .take(1)
                    .subscribe(
                            new Action1<Pair<NotificationListKey, PaginatedNotificationDTO>>()
                            {
                                @Override public void call(
                                        Pair<NotificationListKey, PaginatedNotificationDTO> pair)
                                {
                                    NotificationsView.this.onNotificationsFetched(pair);
                                }
                            },
                            new Action1<Throwable>()
                            {
                                @Override public void call(Throwable error)
                                {
                                    NotificationsView.this.onNotificationsFetchError(error);
                                }
                            }));
        }
    }

    private void refresh()
    {
        notificationListAdapter.clear();
        notificationListAdapter.notifyDataSetChanged();
        PaginatedNotificationListKey firstPage = new PaginatedNotificationListKey(notificationListKey, 1);
        subscriptionList.add(notificationListCache.get()
                .get(firstPage)
                .observeOn(AndroidSchedulers.mainThread())
                .take(1)
                .subscribe(
                        new Action1<Pair<NotificationListKey, PaginatedNotificationDTO>>()
                        {
                            @Override public void call(
                                    Pair<NotificationListKey, PaginatedNotificationDTO> pair)
                            {
                                NotificationsView.this.onNotificationsFetched(pair);
                            }
                        },
                        new Action1<Throwable>()
                        {
                            @Override public void call(Throwable error)
                            {
                                NotificationsView.this.onNotificationsFetchError(error);
                            }
                        }));
        requestUpdateTabCounter();
    }

    protected void onNotificationsFetched(Pair<NotificationListKey, PaginatedNotificationDTO> pair)
    {
        if (pair.second != null)
        {
            //TODO right?
            nextPageDelta = pair.second.getData().isEmpty() ? -1 : 1;

            notificationListAdapter.addAll(pair.second.getData());
            notificationListAdapter.notifyDataSetChanged();
            setReadAllLayoutVisible();
        }
        onNotificationFetchFinish();
    }

    protected void onNotificationsFetchError(Throwable e)
    {
        onNotificationFetchFinish();

        nextPageDelta = 0;
        THToast.show(new THException(e));
    }

    private void onNotificationFetchFinish()
    {
        loading = false;
        swipeRefreshLayout.setRefreshing(false);

        // a bit hacky here to identify whether the list is forced to be refreshed
        Integer currentPage = paginatedNotificationListKey.getPage();
        if (currentPage == null || currentPage != 1 || notificationListAdapter.getCount() > 0)
        {
            setNotificationListShow();
        }
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
                        .subscribe(
                                new Action1<BaseResponseDTO>()
                                {
                                    @Override public void call(BaseResponseDTO response)
                                    {
                                        NotificationsView.this.onNotificationMarkedAsRead(response);
                                    }
                                },
                                new ToastOnErrorAction()));
    }

    protected void onNotificationMarkedAsRead(BaseResponseDTO baseResponseDTO)
    {
        if (notificationListAdapter != null)
        {
            notificationListAdapter.notifyDataSetChanged();
        }
        requestUpdateTabCounter();
    }

    protected void reportNotificationReadAll()
    {
        subscriptionList.add(
                notificationServiceWrapper.markAsReadAllRx(
                        currentUserId.toUserBaseKey())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                new Action1<BaseResponseDTO>()
                                {
                                    @Override public void call(BaseResponseDTO response)
                                    {
                                        NotificationsView.this.updateAllAsRead();
                                    }
                                },
                                new ToastOnErrorAction()
                        ));

        //Mark this locally as read, makes the user feels it's marked instantly for better experience
        updateAllAsRead();
    }

    private void updateAllAsRead()
    {
        setAllNotificationRead();
        setReadAllLayoutVisible();
        requestUpdateTabCounter();
    }

    private void setNotificationListShow()
    {
        setDisplayedChildByLayoutId(R.id.listViewLayout);
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
        boolean haveUnread = false;
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
