package com.tradehero.th.fragments.watchlist;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.Optional;
import com.fortysevendeg.swipelistview.BaseSwipeListViewListener;
import com.fortysevendeg.swipelistview.SwipeListView;
import com.fortysevendeg.swipelistview.SwipeListViewListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshSwipeListView;
import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.common.utils.THToast;
import com.tradehero.common.widget.TwoStateView;
import com.tradehero.th.R;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.portfolio.PortfolioDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.watchlist.WatchlistPositionDTO;
import com.tradehero.th.api.watchlist.WatchlistPositionDTOList;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.fragments.security.SecuritySearchWatchlistFragment;
import com.tradehero.th.fragments.security.WatchlistEditFragment;
import com.tradehero.th.persistence.portfolio.PortfolioCacheRx;
import com.tradehero.th.persistence.watchlist.UserWatchlistPositionCache;
import com.tradehero.th.utils.metrics.Analytics;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import com.tradehero.th.utils.metrics.events.SimpleEvent;
import javax.inject.Inject;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import rx.Observer;
import rx.android.observables.AndroidObservable;

public class WatchlistPositionFragment extends DashboardFragment
{
    private static final String BUNDLE_KEY_SHOW_PORTFOLIO_ID_BUNDLE = WatchlistPositionFragment.class.getName() + ".showPortfolioId";
    private static final int NUMBER_OF_WATCHLIST_SWIPE_BUTTONS_BEHIND = 2;

    @InjectView(android.R.id.empty) @Optional protected ProgressBar progressBar;

    @Inject UserWatchlistPositionCache userWatchlistPositionCache;
    @Inject PortfolioCacheRx portfolioCache;
    @Inject CurrentUserId currentUserId;
    @Inject Analytics analytics;

    @Nullable private DTOCacheNew.Listener<UserBaseKey, WatchlistPositionDTOList> userWatchlistPositionFetchListener;
    @Nullable private DTOCacheNew.Listener<UserBaseKey, WatchlistPositionDTOList> userWatchlistPositionRefreshListener;

    @InjectView(R.id.watchlist_position_list_header) WatchlistPortfolioHeaderView watchlistPortfolioHeaderView;
    @InjectView(R.id.pull_to_refresh_watchlist_listview) PullToRefreshSwipeListView watchlistPositionListView;

    private WatchlistAdapter watchListAdapter;

    private TwoStateView.OnStateChange gainLossModeListener;
    @Nullable private BroadcastReceiver broadcastReceiver;
    private Runnable setOffsetRunnable;

    private OwnedPortfolioId shownPortfolioId;
    private PortfolioDTO shownPortfolioDTO;
    private WatchlistPositionDTOList watchlistPositionDTOs;

    public static void putOwnedPortfolioId(@NonNull Bundle args, @NonNull OwnedPortfolioId ownedPortfolioId)
    {
        args.putBundle(BUNDLE_KEY_SHOW_PORTFOLIO_ID_BUNDLE, ownedPortfolioId.getArgs());
    }

    @NonNull public static OwnedPortfolioId getOwnedPortfolioId(@NonNull Bundle args)
    {
        return new OwnedPortfolioId(args.getBundle(BUNDLE_KEY_SHOW_PORTFOLIO_ID_BUNDLE));
    }

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        gainLossModeListener = createGainLossModeListener();
        broadcastReceiver = createBroadcastReceiver();
        setOffsetRunnable = createSetOffsetRunnable();
        userWatchlistPositionFetchListener = createWatchlistListener();
        userWatchlistPositionRefreshListener = createRefreshWatchlistListener();
    }

    protected TwoStateView.OnStateChange createGainLossModeListener()
    {
        return new TwoStateView.OnStateChange()
        {
            @Override public void onStateChanged(View view, boolean state)
            {
                if (watchListAdapter != null)
                {
                    watchListAdapter.setShowGainLossPercentage(!state);
                    watchListAdapter.notifyDataSetChanged();
                }
            }
        };
    }

    protected BroadcastReceiver createBroadcastReceiver()
    {
        return new BroadcastReceiver()
        {
            @Override public void onReceive(Context context, Intent intent)
            {
                if (watchlistPositionListView != null && watchlistPositionListView.getRefreshableView() != null)
                {
                    SecurityId deletedSecurityId = WatchlistItemView.getDeletedSecurityId(intent);
                    if (deletedSecurityId != null)
                    {
                        SwipeListView watchlistListView = watchlistPositionListView.getRefreshableView();
                        WatchlistAdapter adapter = (WatchlistAdapter) watchlistListView.getAdapter();
                        adapter.remove(deletedSecurityId);
                        adapter.notifyDataSetChanged();
                        analytics.addEvent(new SimpleEvent(AnalyticsConstants.Watchlist_Delete));
                        watchlistListView.closeOpenedItems();
                        fetchWatchlistPositionList();
                    }
                }
            }
        };
    }

    protected Runnable createSetOffsetRunnable()
    {
        return new WatchlistPositionFragmentSetOffsetRunnable();
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.watchlist_positions_list, container, false);

        initViews(view);
        return view;
    }

    private void initViews(View view)
    {
        if (view != null)
        {
            ButterKnife.inject(this, view);

            watchlistPositionListView.post(setOffsetRunnable);
            final SwipeListView watchlistListView = watchlistPositionListView.getRefreshableView();
            watchlistListView.setEmptyView(
                    view.findViewById(R.id.watchlist_position_list_empty_view));
            watchlistListView.setSwipeListViewListener(createSwipeListViewListener());
            initPullToRefreshListView(view);
        }
    }

    private void initPullToRefreshListView(@SuppressWarnings("UnusedParameters") View view)
    {
        watchlistPositionListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<SwipeListView>()
        {
            @Override public void onRefresh(PullToRefreshBase<SwipeListView> refreshView)
            {
                refreshSecurityIdList();
            }
        });
    }

    protected void refreshSecurityIdList()
    {
        detachUserWatchlistRefreshTask();
        userWatchlistPositionCache.register(currentUserId.toUserBaseKey(), userWatchlistPositionRefreshListener);
        userWatchlistPositionCache.getOrFetchAsync(currentUserId.toUserBaseKey(), true);
    }

    protected void fetchWatchlistPositionList()
    {
        detachUserWatchlistFetchTask();
        userWatchlistPositionCache.register(currentUserId.toUserBaseKey(), userWatchlistPositionFetchListener);
        userWatchlistPositionCache.getOrFetchAsync(currentUserId.toUserBaseKey(), false);
    }

    //<editor-fold desc="ActionBar Menu Actions">
    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.position_watchlist_menu, menu);
        setActionBarTitle(getString(R.string.watchlist_title));
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.position_watchlist_add:
            {
                Bundle bundle = new Bundle();
                navigator.get().pushFragment(SecuritySearchWatchlistFragment.class, bundle);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    //</editor-fold>

    @Override public void onResume()
    {
        super.onResume();

        analytics.addEvent(new SimpleEvent(AnalyticsConstants.Watchlist_List));

        LocalBroadcastManager.getInstance(this.getActivity())
                .registerReceiver(broadcastReceiver, new IntentFilter(WatchlistItemView.WATCHLIST_ITEM_DELETED));

        shownPortfolioId = getOwnedPortfolioId(getArguments());
        fetchPortfolio();
        fetchWatchlistPositionList();
    }

    @Override public void onPause()
    {
        super.onPause();

        LocalBroadcastManager.getInstance(this.getActivity())
                .unregisterReceiver(broadcastReceiver);
    }

    @Override public void onDestroyView()
    {
        detachUserWatchlistFetchTask();
        detachUserWatchlistRefreshTask();
        if (watchlistPortfolioHeaderView != null)
        {
            watchlistPortfolioHeaderView.setOnStateChangeListener(null);
        }
        watchlistPortfolioHeaderView = null;

        if (watchlistPositionListView != null)
        {
            watchlistPositionListView.removeCallbacks(setOffsetRunnable);
            SwipeListView swipeListView = watchlistPositionListView.getRefreshableView();
            if (swipeListView != null)
            {
                swipeListView.setSwipeListViewListener(null);
            }
        }

        watchListAdapter = null;

        if (watchlistPositionListView != null)
        {
            View watchListView = watchlistPositionListView.getRefreshableView();
            if (watchListView != null)
            {
                watchListView.removeCallbacks(null);
            }
            watchlistPositionListView.onRefreshComplete();
            watchlistPositionListView.setOnRefreshListener(
                    (PullToRefreshBase.OnRefreshListener<SwipeListView>) null);
        }

        super.onDestroyView();
    }

    protected void detachUserWatchlistFetchTask()
    {
        userWatchlistPositionCache.unregister(userWatchlistPositionFetchListener);
    }

    protected void detachUserWatchlistRefreshTask()
    {
        userWatchlistPositionCache.unregister(userWatchlistPositionRefreshListener);
    }

    @Override public void onDestroy()
    {
        userWatchlistPositionRefreshListener = null;
        userWatchlistPositionFetchListener = null;
        broadcastReceiver = null;
        gainLossModeListener = null;
        setOffsetRunnable = null;

        super.onDestroy();
    }

    protected void fetchPortfolio()
    {
        AndroidObservable.bindFragment(this, portfolioCache.get(shownPortfolioId))
                .subscribe(createPortfolioCacheObserver());
    }

    private void displayHeader()
    {
        if (watchlistPortfolioHeaderView != null)
        {
            watchlistPortfolioHeaderView.linkWith(shownPortfolioDTO, true);
            watchlistPortfolioHeaderView.linkWith(watchlistPositionDTOs, true);
            watchlistPortfolioHeaderView.setOnStateChangeListener(gainLossModeListener);
        }
    }

    private void displayWatchlist(WatchlistPositionDTOList watchlistPositionDTOs)
    {
        this.watchlistPositionDTOs = watchlistPositionDTOs;

        WatchlistAdapter newAdapter = createWatchlistAdapter();
        newAdapter.addAll(watchlistPositionDTOs);
        watchlistPositionListView.setAdapter(newAdapter);
        watchListAdapter = newAdapter;
        watchlistPositionListView.onRefreshComplete();
        displayHeader();
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

    private WatchlistAdapter createWatchlistAdapter()
    {
        return new WatchlistAdapter(getActivity(), R.layout.watchlist_item_view);
    }

    private void displayProgress(boolean show)
    {
        if (progressBar != null)
        {
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    protected SwipeListViewListener createSwipeListViewListener()
    {
        return new WatchlistPositionFragmentSwipeListViewListener();
    }

    protected DTOCacheNew.Listener<UserBaseKey, WatchlistPositionDTOList> createWatchlistListener()
    {
        return new WatchlistPositionFragmentSecurityIdListCacheListener();
    }

    protected class WatchlistPositionFragmentSecurityIdListCacheListener implements DTOCacheNew.Listener<UserBaseKey, WatchlistPositionDTOList>
    {
        @Override public void onDTOReceived(@NonNull UserBaseKey key, @NonNull WatchlistPositionDTOList value)
        {
            displayWatchlist(value);
        }

        @Override public void onErrorThrown(@NonNull UserBaseKey key, @NonNull Throwable error)
        {
            watchlistPositionListView.onRefreshComplete();
            THToast.show(getString(R.string.error_fetch_portfolio_watchlist));
        }
    }

    protected DTOCacheNew.Listener<UserBaseKey, WatchlistPositionDTOList> createRefreshWatchlistListener()
    {
        return new RefreshWatchlisListener();
    }

    protected class RefreshWatchlisListener implements DTOCacheNew.Listener<UserBaseKey, WatchlistPositionDTOList>
    {
        @Override public void onDTOReceived(@NonNull UserBaseKey key, @NonNull WatchlistPositionDTOList value)
        {
            watchlistPositionListView.onRefreshComplete();
            displayWatchlist(value);
        }

        @Override public void onErrorThrown(@NonNull UserBaseKey key, @NonNull Throwable error)
        {
            watchlistPositionListView.onRefreshComplete();
            if (watchListAdapter == null || watchListAdapter.getCount() <= 0)
            {
                THToast.show(getString(R.string.error_fetch_portfolio_watchlist));
            }
        }
    }

    protected Observer<Pair<OwnedPortfolioId, PortfolioDTO>> createPortfolioCacheObserver()
    {
        return new WatchlistPositionFragmentPortfolioCacheObserver();
    }

    protected class WatchlistPositionFragmentPortfolioCacheObserver implements Observer<Pair<OwnedPortfolioId, PortfolioDTO>>
    {
        @Override public void onNext(Pair<OwnedPortfolioId, PortfolioDTO> pair)
        {
            shownPortfolioDTO = pair.second;
            displayHeader();

        }

        @Override public void onCompleted()
        {
        }

        @Override public void onError(Throwable e)
        {
            THToast.show(R.string.error_fetch_portfolio_info);
        }
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

    protected class WatchlistPositionFragmentSetOffsetRunnable implements Runnable
    {
        @Override public void run()
        {
            if (watchlistPositionListView != null)
            {
                SwipeListView watchlistListViewCopy = watchlistPositionListView.getRefreshableView();
                if (watchlistListViewCopy != null)
                {
                    watchlistListViewCopy.setOffsetLeft(watchlistPositionListView.getWidth() -
                            getResources().getDimension(R.dimen.watchlist_item_button_width)
                                    * NUMBER_OF_WATCHLIST_SWIPE_BUTTONS_BEHIND);
                }
            }
        }
    }
}
