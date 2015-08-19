package com.tradehero.th.fragments.watchlist;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ProgressBar;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.fortysevendeg.swipelistview.BaseSwipeListViewListener;
import com.fortysevendeg.swipelistview.SwipeListView;
import com.fortysevendeg.swipelistview.SwipeListViewListener;
import com.tradehero.common.rx.PairGetSecond;
import com.tradehero.common.utils.THToast;
import com.tradehero.metrics.Analytics;
import com.tradehero.th.R;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.watchlist.WatchlistPositionDTO;
import com.tradehero.th.api.watchlist.WatchlistPositionDTOList;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.fragments.security.SecuritySearchWatchlistFragment;
import com.tradehero.th.fragments.security.WatchlistEditFragment;
import com.tradehero.th.fragments.trending.TrendingMainFragment;
import com.tradehero.th.persistence.watchlist.UserWatchlistPositionCacheRx;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import com.tradehero.th.utils.metrics.events.SimpleEvent;
import com.tradehero.th.widget.MultiScrollListener;
import javax.inject.Inject;
import rx.android.app.AppObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import timber.log.Timber;

public class MainWatchlistPositionFragment extends DashboardFragment
{
    private static final String BUNDLE_KEY_SHOW_ACTION_BAR_TITLE = MainWatchlistPositionFragment.class.getName() + ".showActionBarTitle";
    private static final boolean DEFAULT_SHOW_ACTION_BAR_TITLE = false;
    private static final int NUMBER_OF_WATCHLIST_SWIPE_BUTTONS_BEHIND = 2;

    @Inject CurrentUserId currentUserId;
    @Inject UserWatchlistPositionCacheRx userWatchlistPositionCache;
    @Inject Analytics analytics;

    @Bind(android.R.id.empty) @Nullable protected ProgressBar progressBar;
    @Bind(R.id.watchlist_swipe_listview) SwipeListView watchlistPositionListView;
    @Bind(R.id.swipe_container) SwipeRefreshLayout watchListRefreshableContainer;

    private WatchlistAdapter watchListAdapter;
    private BroadcastReceiver broadcastReceiver;

    private WatchlistPositionDTOList watchlistPositionDTOs;

    public static void putShowActionBarTitle(@NonNull Bundle args, boolean showActionBarTitle)
    {
        args.putBoolean(BUNDLE_KEY_SHOW_ACTION_BAR_TITLE, showActionBarTitle);
    }

    public static boolean getShowActionBarTitle(@NonNull Bundle args)
    {
        return args.getBoolean(BUNDLE_KEY_SHOW_ACTION_BAR_TITLE, DEFAULT_SHOW_ACTION_BAR_TITLE);
    }

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        watchListAdapter = createWatchlistAdapter();
        broadcastReceiver = createBroadcastReceiver();
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.watchlist_positions_list, container, false);
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        watchlistPositionListView.post(new Runnable()
        {
            @Override public void run()
            {
                MainWatchlistPositionFragment.this.setWatchlistOffset();
            }
        });
        watchlistPositionListView.setEmptyView(view.findViewById(R.id.watchlist_position_list_empty_view));
        watchlistPositionListView.setOnScrollListener(createListViewScrollListener());
        watchlistPositionListView.setAdapter(watchListAdapter);
        watchlistPositionListView.setSwipeListViewListener(createSwipeListViewListener());
        watchListRefreshableContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override public void onRefresh()
            {
                MainWatchlistPositionFragment.this.refreshValues();
            }
        });
    }

    //<editor-fold desc="ActionBar Menu Actions">
    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        setActionBarTitle(R.string.watchlist_title);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.position_watchlist_add:
            {
                Bundle bundle = new Bundle();
                if (navigator.get().hasBackStackName(getClass().getName()))
                {
                    SecuritySearchWatchlistFragment.putReturnFragment(bundle, getClass());
                }
                else if (navigator.get().hasBackStackName(TrendingMainFragment.class.getName()))
                {
                    SecuritySearchWatchlistFragment.putReturnFragment(bundle, TrendingMainFragment.class);
                }
                else
                {
                    Timber.e(new RuntimeException(), "No return fragment found");
                }
                navigator.get().pushFragment(SecuritySearchWatchlistFragment.class, bundle);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    //</editor-fold>

    @Override public void onStart()
    {
        super.onStart();
        fetchWatchlistPositionList();
    }

    @Override public void onResume()
    {
        super.onResume();
        analytics.addEvent(new SimpleEvent(AnalyticsConstants.Watchlist_List));
        LocalBroadcastManager.getInstance(this.getActivity())
                .registerReceiver(broadcastReceiver, new IntentFilter(WatchlistItemView.WATCHLIST_ITEM_DELETED));
    }

    @Override public void onPause()
    {
        super.onPause();
        LocalBroadcastManager.getInstance(this.getActivity())
                .unregisterReceiver(broadcastReceiver);
    }

    @Override public void onDestroyView()
    {
        watchlistPositionListView.removeCallbacks(null);
        watchlistPositionListView.setOnScrollListener(null);
        watchlistPositionListView.setSwipeListViewListener(null);
        watchlistPositionListView.removeCallbacks(null);

        watchListRefreshableContainer.setRefreshing(false);
        watchListRefreshableContainer.setOnRefreshListener(null);

        ButterKnife.unbind(this);
        super.onDestroyView();
    }

    @Override public void onDestroy()
    {
        broadcastReceiver = null;
        watchListAdapter = null;

        super.onDestroy();
    }

    @NonNull private WatchlistAdapter createWatchlistAdapter()
    {
        return new WatchlistAdapter(getActivity(), R.layout.watchlist_item_view);
    }

    @NonNull protected BroadcastReceiver createBroadcastReceiver()
    {
        return new BroadcastReceiver()
        {
            @Override public void onReceive(Context context, Intent intent)
            {
                if (watchlistPositionListView != null)
                {
                    SecurityId deletedSecurityId = WatchlistItemView.getDeletedSecurityId(intent);
                    if (deletedSecurityId != null)
                    {
                        WatchlistAdapter adapter = (WatchlistAdapter) watchlistPositionListView.getAdapter();
                        adapter.remove(deletedSecurityId);
                        adapter.notifyDataSetChanged();
                        analytics.addEvent(new SimpleEvent(AnalyticsConstants.Watchlist_Delete));
                        watchlistPositionListView.closeOpenedItems();
                        fetchWatchlistPositionList();
                    }
                }
            }
        };
    }

    @NonNull protected AbsListView.OnScrollListener createListViewScrollListener()
    {
        return new MultiScrollListener(fragmentElements.get().getListViewScrollListener(),
                new AbsListView.OnScrollListener()
                {
                    int maxOffsetY = 0;

                    @Override
                    public void onScrollStateChanged(AbsListView view, int scrollState)
                    {
                        int firstVisibleItem = view.getFirstVisiblePosition();
                        if (firstVisibleItem == 0)
                        {
                            View child = view.getChildAt(firstVisibleItem);
                            if (child != null)
                            {
                                int offsetY = child.getTop();
                                if (offsetY > maxOffsetY)
                                {
                                    maxOffsetY = offsetY;
                                }
                                watchListRefreshableContainer.setEnabled(offsetY == maxOffsetY);
                            }
                            else
                            {
                                Timber.e(new NullPointerException(), "Child was null");
                            }
                        }
                        else
                        {
                            watchListRefreshableContainer.setEnabled(false);
                        }
                    }

                    @Override
                    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
                    {
                    }
                });
    }

    public void setWatchlistOffset()
    {
        if (watchlistPositionListView != null)
        {
            watchlistPositionListView.setOffsetLeft(watchlistPositionListView.getWidth() -
                    getResources().getDimension(R.dimen.watchlist_item_button_width)
                            * NUMBER_OF_WATCHLIST_SWIPE_BUTTONS_BEHIND);
        }
    }

    protected SwipeListViewListener createSwipeListViewListener()
    {
        return new WatchlistPositionFragmentSwipeListViewListener();
    }

    protected class WatchlistPositionFragmentSwipeListViewListener extends BaseSwipeListViewListener
    {
        @Override public void onClickFrontView(int position)
        {
            super.onClickFrontView(position);
            openWatchlistItemEditor(position);
        }

        @Override public void onStartOpen(int position, int action, boolean right)
        {
            analytics.addEvent(new SimpleEvent(AnalyticsConstants.Watchlist_CellSwipe));
            super.onStartOpen(position, action, right);
        }

        @Override public void onDismiss(int[] reverseSortedPositions)
        {
            super.onDismiss(reverseSortedPositions);
            fetchWatchlistPositionList();
        }
    }

    protected void fetchWatchlistPositionList()
    {
        onStopSubscriptions.add(AppObservable.bindSupportFragment(
                this,
                userWatchlistPositionCache.get(currentUserId.toUserBaseKey())
                        .map(new PairGetSecond<UserBaseKey, WatchlistPositionDTOList>()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Action1<WatchlistPositionDTOList>()
                        {
                            @Override public void call(WatchlistPositionDTOList list)
                            {
                                MainWatchlistPositionFragment.this.displayWatchlist(list);
                            }
                        },
                        new Action1<Throwable>()
                        {
                            @Override public void call(Throwable e)
                            {
                                watchListRefreshableContainer.setRefreshing(false);
                                if (watchListAdapter == null || watchListAdapter.getCount() <= 0)
                                {
                                    THToast.show(MainWatchlistPositionFragment.this.getString(R.string.error_fetch_portfolio_watchlist));
                                }
                            }
                        }));
    }

    protected void refreshValues()
    {
        userWatchlistPositionCache.invalidate(currentUserId.toUserBaseKey());
        userWatchlistPositionCache.get(currentUserId.toUserBaseKey());
    }

    private void displayWatchlist(WatchlistPositionDTOList watchlistPositionDTOs)
    {
        this.watchlistPositionDTOs = watchlistPositionDTOs;
        watchListAdapter.clear();
        watchListAdapter.addAll(watchlistPositionDTOs);
        watchListAdapter.notifyDataSetChanged();
        watchListRefreshableContainer.setRefreshing(false);
    }

    private void openWatchlistItemEditor(int position)
    {
        // TODO discover why sometimes we would get a mismatch
        if (position < watchListAdapter.getCount())
        {
            WatchlistPositionDTO watchlistPositionDTO = watchListAdapter.getItem(position);
            Bundle args = new Bundle();
            if (watchlistPositionDTO != null)
            {
                WatchlistEditFragment.putSecurityId(args, watchlistPositionDTO.securityDTO.getSecurityId());
            }
            navigator.get().pushFragment(WatchlistEditFragment.class, args, null);
        }
    }
}
