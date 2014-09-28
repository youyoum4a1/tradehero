package com.tradehero.th.fragments.chinabuild.fragment.message;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.common.utils.THToast;
import com.tradehero.common.widget.BetterViewAnimator;
import com.tradehero.th.R;
import com.tradehero.th.adapters.NotificationListAdapter;
import com.tradehero.th.api.notification.NotificationDTO;
import com.tradehero.th.api.notification.NotificationKey;
import com.tradehero.th.api.notification.NotificationListKey;
import com.tradehero.th.api.notification.PaginatedNotificationDTO;
import com.tradehero.th.api.notification.PaginatedNotificationListKey;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.fragments.chinabuild.listview.SecurityListView;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.network.retrofit.MiddleCallbackWeakList;
import com.tradehero.th.network.service.NotificationServiceWrapper;
import com.tradehero.th.persistence.notification.NotificationListCache;
import com.tradehero.th.persistence.user.UserProfileCache;
import dagger.Lazy;
import java.util.ArrayList;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

public class NotificationFragment extends DashboardFragment
{
    @InjectView(android.R.id.progress) ProgressBar progressBar;
    @InjectView(R.id.listNotification) SecurityListView listView;
    @InjectView(R.id.bvaViewAll) BetterViewAnimator betterViewAnimator;

    @Inject Lazy<NotificationListCache> notificationListCache;
    @Inject NotificationServiceWrapper notificationServiceWrapper;
    @Inject UserProfileCache userProfileCache;
    @Inject CurrentUserId currentUserId;

    private PaginatedNotificationListKey paginatedNotificationListKey;
    private int currentPage;

    @NotNull private MiddleCallbackWeakList<Response> middleCallbacks;
    private DTOCacheNew.Listener<NotificationListKey, PaginatedNotificationDTO> notificationListFetchListener;
    private DTOCacheNew.Listener<NotificationListKey, PaginatedNotificationDTO> notificationListRefreshListener;
    private NotificationListKey notificationListKey;
    private PullToRefreshBase.OnRefreshListener<ListView> notificationPullToRefreshListener;

    private NotificationListAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        middleCallbacks = new MiddleCallbackWeakList<>();
        notificationListFetchListener = createNotificationFetchListener();
        notificationListRefreshListener = createNotificationRefreshListener();
        adapter = new NotificationListAdapter(getActivity());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        setHeadViewMiddleMain("通知");
        setHeadViewRight0("全部已读");
    }

    @Override
    public void onClickHeadRight0()
    {
        Timber.d("全部已读");
        reportNotificationReadAll();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.notification_fragment, container, false);
        ButterKnife.inject(this, view);
        initView();
        if (adapter.getCount() == 0)
        {
            betterViewAnimator.setDisplayedChildByLayoutId(R.id.progress);
            resetPage();
            refresh();
        }
        else
        {
            betterViewAnimator.setDisplayedChildByLayoutId(R.id.listNotification);
        }
        return view;
    }

    public void initView()
    {
        listView.setMode(PullToRefreshBase.Mode.BOTH);
        listView.setAdapter(adapter);
        adapter.setNotificationLister(new NotificationListAdapter.NotificationClickListener()
        {
            @Override public void OnNotificationItemClicked(int position)
            {
                if(((NotificationDTO) adapter.getItem(position)).unread){
                    reportNotificationRead(((NotificationDTO) adapter.getItem(position)).pushId);
                }
            }
        });
        listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>()
        {
            @Override public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView)
            {
                Timber.d("下拉刷新");
                refresh();
            }

            @Override public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView)
            {
                Timber.d("上拉加载更多");
                fetchNextPageIfNecessary();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override public void onItemClick(AdapterView<?> adapterView, View view, int i, long position)
            {
                NotificationDTO dto = (NotificationDTO) adapter.getItem((int) position);
                enterNotification(dto);
            }
        });
    }

    public void enterNotification(NotificationDTO dto)
    {
        reportNotificationRead(dto.pushId);
    }

    @Override public void onStop()
    {
        super.onStop();
    }

    @Override public void onDestroyView()
    {
        detachNotificationListFetchTask();
        detachNotificationListRefreshTask();

        ButterKnife.reset(this);
        super.onDestroyView();
    }

    @Override public void onDestroy()
    {
        super.onDestroy();
    }

    @Override public void onResume()
    {
        super.onResume();
    }

    private void initListData(PaginatedNotificationDTO value, NotificationListKey key)
    {
        if (value != null && ((PaginatedNotificationListKey) key).getPage() == 1)
        {
            ArrayList<NotificationDTO> list = new ArrayList<NotificationDTO>();
            list.addAll(value.getData());
            adapter.setListData(list);
            currentPage = ((PaginatedNotificationListKey) key).getPage();
        }
        else if (value != null && ((PaginatedNotificationListKey) key).getPage() > 1)
        {
            ArrayList<NotificationDTO> list = new ArrayList<NotificationDTO>();
            list.addAll(value.getData());
            adapter.addListData(list);
            currentPage = ((PaginatedNotificationListKey) key).getPage();
        }
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
            //if (value != null)
            //{
            //    //TODO right?
            //    if(value.getData().isEmpty())
            //    {
            //        listView.onRefreshComplete();
            //    }
            //}
            initListData(value, key);
        }

        @Override public void onErrorThrown(@NotNull NotificationListKey key, @NotNull Throwable error)
        {
            onFinish();
            THToast.show(new THException(error));
        }

        private void onFinish()
        {
            betterViewAnimator.setDisplayedChildByLayoutId(R.id.listNotification);
            listView.onRefreshComplete();
        }
    }

    private DTOCacheNew.Listener<NotificationListKey, PaginatedNotificationDTO> createNotificationRefreshListener()
    {
        return new NotificationRefreshListener();
    }

    private class NotificationRefreshListener implements DTOCacheNew.Listener<NotificationListKey, PaginatedNotificationDTO>
    {
        @Override public void onDTOReceived(@NotNull NotificationListKey key, @NotNull PaginatedNotificationDTO value)
        {
            initListData(value, key);
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
            if (listView != null)
            {
                listView.onRefreshComplete();
            }
            betterViewAnimator.setDisplayedChildByLayoutId(R.id.listNotification);
            listView.onRefreshComplete();
        }
    }

    private void resetPage()
    {
        if (notificationListKey == null)
        {
            notificationListKey = new NotificationListKey();
        }
        currentPage = 0;
        paginatedNotificationListKey = new PaginatedNotificationListKey(notificationListKey, 1);
    }

    protected Callback<Response> createMarkNotificationAsReadCallback()
    {
        return new NotificationMarkAsReadCallback();
    }

    protected class NotificationMarkAsReadCallback implements Callback<Response>
    {
        @Override public void success(Response response, Response response2)
        {
            Timber.d("已读");
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
            Timber.d("全部已读");
            adapter.setAllRead();
        }

        @Override public void failure(RetrofitError retrofitError)
        {
            Timber.d("NotificationMarkAsReadAllCallback failure");
        }
    }

    private void refresh()
    {
        detachNotificationListRefreshTask();
        PaginatedNotificationListKey firstPage = new PaginatedNotificationListKey(notificationListKey, 1);
        notificationListCache.get().register(firstPage, notificationListRefreshListener);
        notificationListCache.get().getOrFetchAsync(firstPage, true);
    }

    private void fetchNextPageIfNecessary()
    {
        fetchNextPageIfNecessary(true);
    }

    public void fetchNextPageIfNecessary(boolean force)
    {
        detachNotificationListFetchTask();

        if (paginatedNotificationListKey == null)
        {
            paginatedNotificationListKey = new PaginatedNotificationListKey(notificationListKey, 1);
        }

        if (currentPage >= 1)
        {
            paginatedNotificationListKey = new PaginatedNotificationListKey(notificationListKey, currentPage + 1);
            notificationListCache.get().register(paginatedNotificationListKey, notificationListFetchListener);
            notificationListCache.get().getOrFetchAsync(paginatedNotificationListKey, force);
        }
    }

    private void detachNotificationListFetchTask()
    {
        notificationListCache.get().unregister(notificationListFetchListener);
    }

    private void detachNotificationListRefreshTask()
    {
        notificationListCache.get().unregister(notificationListRefreshListener);
    }

    protected void reportNotificationRead(int pushId)
    {
        middleCallbacks.add(
                notificationServiceWrapper.markAsRead(
                        currentUserId.toUserBaseKey(),
                        new NotificationKey(pushId),
                        createMarkNotificationAsReadCallback()));

        adapter.setHasRead(pushId);
    }

    protected void reportNotificationReadAll()
    {
        middleCallbacks.add(
                notificationServiceWrapper.markAsReadAll(
                        currentUserId.toUserBaseKey(),
                        createMarkNotificationAsReadAllCallback()));
    }
}
