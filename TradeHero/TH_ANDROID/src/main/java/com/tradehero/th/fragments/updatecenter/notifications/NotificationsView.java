package com.tradehero.th.fragments.updatecenter.notifications;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.handmark.pulltorefresh.library.pulltorefresh.PullToRefreshBase;
import com.handmark.pulltorefresh.library.pulltorefresh.PullToRefreshListView;
import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.common.utils.THToast;
import com.tradehero.common.widget.BetterViewAnimator;
import com.tradehero.th.R;
import com.tradehero.th.api.notification.NotificationDTO;
import com.tradehero.th.api.notification.NotificationKey;
import com.tradehero.th.api.notification.NotificationListKey;
import com.tradehero.th.api.notification.PaginatedNotificationDTO;
import com.tradehero.th.api.notification.PaginatedNotificationListKey;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.fragments.updatecenter.UpdateCenterFragment;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.network.retrofit.MiddleCallbackWeakList;
import com.tradehero.th.network.service.NotificationServiceWrapper;
import com.tradehero.th.persistence.notification.NotificationListCache;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.utils.DaggerUtils;
import com.tradehero.th.utils.EndlessScrollingHelper;
import dagger.Lazy;
import java.util.List;
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
    @InjectView(R.id.listViewLayout) RelativeLayout listViewLayout;
    @InjectView(R.id.readAllLayout) LinearLayout readAllLayout;


    @Inject Lazy<NotificationListCache> notificationListCache;
    @Inject NotificationServiceWrapper notificationServiceWrapper;
    @Inject UserProfileCache userProfileCache;
    @Inject CurrentUserId currentUserId;

    private PaginatedNotificationListKey paginatedNotificationListKey;
    private boolean loading;
    private int nextPageDelta;

    @NotNull private MiddleCallbackWeakList<Response> middleCallbacks;
    private DTOCacheNew.Listener<NotificationListKey, PaginatedNotificationDTO> notificationListFetchListener;
    private DTOCacheNew.Listener<NotificationListKey, PaginatedNotificationDTO> notificationListRefreshListener;
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
        ButterKnife.inject(this);

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

    @OnClick(R.id.readAllLayout)
    protected void onReadAllLayoutClicked()
    {
        reportNotificationReadAll();
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

    private void resetContent(List<NotificationDTO> notificationKeyList)
    {
        if (notificationListAdapter == null)
        {
            notificationListAdapter = createNotificationListAdapter();
        }
        notificationListAdapter.clear();
        notificationListAdapter.addAll(notificationKeyList);
        notificationListAdapter.notifyDataSetChanged();
        setReadAllLayoutVisable();
        Timber.d("resetContent");
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
                        currentUserId.toUserBaseKey(),
                        new NotificationKey(pushId),
                        createMarkNotificationAsReadCallback()));
    }

    protected void reportNotificationReadAll()
    {
        middleCallbacks.add(
                notificationServiceWrapper.markAsReadAll(
                        currentUserId.toUserBaseKey(),
                        createMarkNotificationAsReadAllCallback()));
    }

    private DTOCacheNew.Listener<NotificationListKey, PaginatedNotificationDTO> createNotificationFetchListener()
    {
        return new NotificationFetchListener(true);
    }

    private class NotificationFetchListener implements DTOCacheNew.Listener<NotificationListKey, PaginatedNotificationDTO>
    {
        private final boolean shouldAppend;

        public NotificationFetchListener(boolean shouldAppend)
        {
            this.shouldAppend = shouldAppend;
        }

        @Override public void onDTOReceived(@NotNull NotificationListKey key, @NotNull PaginatedNotificationDTO value)
        {
            onFinish();

            if (value != null)
            {
                //TODO right?
                nextPageDelta = value.getData().isEmpty() ? -1 : 1;

                if (!shouldAppend)
                {
                    notificationListAdapter.clear();
                }
                notificationListAdapter.addAll(value.getData());
                notificationListAdapter.notifyDataSetChanged();
                setReadAllLayoutVisable();
            }
        }

        @Override public void onErrorThrown(@NotNull NotificationListKey key, @NotNull Throwable error)
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
            setNotificationListShow();
        }
    }

    private void setNotificationListShow()
    {
        setDisplayedChildByLayoutId(R.id.listViewLayout);
    }

    private DTOCacheNew.Listener<NotificationListKey, PaginatedNotificationDTO> createNotificationRefreshListener()
    {
        return new NotificationRefreshListener();
    }

    private class NotificationRefreshListener implements DTOCacheNew.Listener<NotificationListKey, PaginatedNotificationDTO>
    {
        @Override public void onDTOReceived(@NotNull NotificationListKey key, @NotNull PaginatedNotificationDTO paginatedNotificationDTO)
        {
            resetPage();
            resetContent(paginatedNotificationDTO.getData());
            onFinish();
        }

        @Override public void onErrorThrown(@NotNull NotificationListKey key, @NotNull Throwable error)
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
            if (notificationListAdapter != null)
            {
                notificationListAdapter.notifyDataSetChanged();
            }
            requestUpdateTabCounter();
        }

        @Override public void failure(RetrofitError retrofitError)
        {
        }
    }

    protected Callback<Response> createMarkNotificationAsReadAllCallback()
    {
        return new NotificationMarkAsReadAllCallback();
    }

    protected class NotificationMarkAsReadAllCallback implements Callback<Response>
    {
        @Override public void success(Response response, Response response2)
        {
            Timber.d("NotificationMarkAsReadAllCallback success");
            setAllNotificationRead();
            setReadAllLayoutVisable();
            requestUpdateTabCounter();
        }

        @Override public void failure(RetrofitError retrofitError)
        {
            Timber.d("NotificationMarkAsReadAllCallback failure");
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
            NotificationDTO notificationDTO = (NotificationDTO) parent.getItemAtPosition(position);
            NotificationClickHandler notificationClickHandler = new NotificationClickHandler(getContext(), notificationDTO);
            notificationClickHandler.handleNotificationItemClicked();
            reportNotificationRead(notificationDTO.pushId);
        }
    }

    private void setReadAllLayoutVisable()
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
