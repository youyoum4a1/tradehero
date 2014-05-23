package com.tradehero.th.fragments.watchlist;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.Optional;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.fortysevendeg.android.swipelistview.BaseSwipeListViewListener;
import com.fortysevendeg.android.swipelistview.SwipeListView;
import com.fortysevendeg.android.swipelistview.SwipeListViewListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshSwipeListView;
import com.localytics.android.LocalyticsSession;
import com.tradehero.common.milestone.Milestone;
import com.tradehero.common.persistence.DTOCache;
import com.tradehero.common.utils.THToast;
import com.tradehero.common.widget.TwoStateView;
import com.tradehero.th.R;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.portfolio.PortfolioDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.security.SecurityIdList;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.base.Navigator;
import com.tradehero.th.fragments.base.BaseFragment;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.fragments.portfolio.header.PortfolioHeaderFactory;
import com.tradehero.th.fragments.security.SecuritySearchWatchlistFragment;
import com.tradehero.th.fragments.security.WatchlistEditFragment;
import com.tradehero.th.persistence.portfolio.PortfolioCache;
import com.tradehero.th.persistence.watchlist.UserWatchlistPositionCache;
import com.tradehero.th.persistence.watchlist.WatchlistPositionCache;
import com.tradehero.th.persistence.watchlist.WatchlistRetrievedMilestone;
import com.tradehero.th.utils.metrics.localytics.LocalyticsConstants;
import javax.inject.Inject;

public class WatchlistPositionFragment extends DashboardFragment
    implements BaseFragment.TabBarVisibilityInformer
{
    private static final String BUNDLE_KEY_SHOW_PORTFOLIO_ID_BUNDLE = WatchlistPositionFragment.class.getName() + ".showPortfolioId";
    private static final int NUMBER_OF_WATCHLIST_SWIPE_BUTTONS_BEHIND = 2;

    @InjectView(android.R.id.empty) @Optional protected ProgressBar progressBar;

    @Inject WatchlistPositionCache watchlistPositionCache;
    @Inject UserWatchlistPositionCache userWatchlistPositionCache;
    @Inject PortfolioCache portfolioCache;
    @Inject PortfolioHeaderFactory headerFactory;
    @Inject CurrentUserId currentUserId;
    @Inject LocalyticsSession localyticsSession;

    private DTOCache.GetOrFetchTask<UserBaseKey, SecurityIdList> userWatchlistPositionFetchTask;
    private DTOCache.GetOrFetchTask<OwnedPortfolioId, PortfolioDTO> portfolioFetchTask;

    //@InjectView(android.R.id.list) SwipeListView watchlistListView;
    @InjectView(R.id.watchlist_position_list_header) WatchlistPortfolioHeaderView watchlistPortfolioHeaderView;
    @InjectView(R.id.pull_to_refresh_watchlist_listview) PullToRefreshSwipeListView watchlistPositionListView;

    private WatchlistAdapter watchListAdapter;

    private WatchlistRetrievedMilestone watchlistRetrievedMilestone;
    private TwoStateView.OnStateChange gainLossModeListener;
    private BroadcastReceiver broadcastReceiver;
    private Runnable setOffsetRunnable;

    private OwnedPortfolioId shownPortfolioId;
    private PortfolioDTO shownPortfolioDTO;

    public static void putOwnedPortfolioId(Bundle args, OwnedPortfolioId ownedPortfolioId)
    {
        args.putBundle(BUNDLE_KEY_SHOW_PORTFOLIO_ID_BUNDLE, ownedPortfolioId.getArgs());
    }

    public static OwnedPortfolioId getOwnedPortfolioId(Bundle args)
    {
        return new OwnedPortfolioId(args.getBundle(BUNDLE_KEY_SHOW_PORTFOLIO_ID_BUNDLE));
    }

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        gainLossModeListener = createGainLossModeListener();
        broadcastReceiver =createBroadcastReceiver();
        setOffsetRunnable = createSetOffsetRunnable();
    }

    protected Milestone.OnCompleteListener createWatchlistRetrievedMilestoneListener()
    {
        return new Milestone.OnCompleteListener()
        {
            @Override public void onComplete(Milestone milestone)
            {
                display();
            }

            @Override public void onFailed(Milestone milestone, Throwable throwable)
            {
                displayProgress(false);
            }
        };
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
                        SwipeListView watchlistListView = (SwipeListView) watchlistPositionListView.getRefreshableView();
                        WatchlistAdapter adapter = (WatchlistAdapter) watchlistListView.getAdapter();
                        adapter.remove(deletedSecurityId);
                        localyticsSession.tagEvent(LocalyticsConstants.Watchlist_Delete);
                        watchlistListView.closeOpenedItems();
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

            final SwipeListView watchlistListView = watchlistPositionListView.getRefreshableView();
            watchlistListView.post(setOffsetRunnable);
            watchlistListView.setEmptyView(
                    view.findViewById(R.id.watchlist_position_list_empty_view));
            watchlistListView.setSwipeListViewListener(createSwipeListViewListener());
            initPullToRefreshListView(view);
        }
    }

    private void initPullToRefreshListView(View view)
    {
        // wrong usage
        //((ViewGroup) view).removeView(watchlistListView);
        //watchlistPositionListView.setRefreshableView(watchlistListView);

        watchlistPositionListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<SwipeListView>()
        {
            @Override public void onRefresh(PullToRefreshBase<SwipeListView> refreshView)
            {
                refretchSecurityIdList();
            }
        });
    }

    protected void refretchSecurityIdList()
    {
        detachUserWatchlistFetchTask();
        userWatchlistPositionFetchTask = userWatchlistPositionCache.getOrFetch(
                currentUserId.toUserBaseKey(),
                true,
                new RefreshWatchlisListener());
        userWatchlistPositionFetchTask.execute();
    }

    protected void fetchSecurityIdList()
    {
        detachUserWatchlistFetchTask();
        userWatchlistPositionFetchTask = userWatchlistPositionCache.getOrFetch(
                currentUserId.toUserBaseKey(),
                true,
                createSecurityIdListCacheListener());
        userWatchlistPositionFetchTask.execute();
    }

    //<editor-fold desc="ActionBar Menu Actions">
    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.position_watchlist_menu, menu);
        ActionBar actionBar = getSherlockActivity().getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);
        actionBar.setTitle(getString(R.string.watchlist_title));
        super.onCreateOptionsMenu(menu, inflater);

        MenuItem menuItem = menu.findItem(R.id.position_watchlist_add);
        View menuAddWatchlist = menuItem.getActionView().findViewById(R.id.position_watchlist_add_view);
        if (menuAddWatchlist != null)
        {
            menuAddWatchlist.setOnClickListener(new View.OnClickListener()
            {
                @Override public void onClick(View v)
                {
                    Bundle bundle = new Bundle();
                    getNavigator().pushFragment(SecuritySearchWatchlistFragment.class, bundle);
                }
            });
        }
    }
    //</editor-fold>

    @Override public void onResume()
    {
        super.onResume();

        localyticsSession.tagEvent(LocalyticsConstants.Watchlist_List);

        LocalBroadcastManager.getInstance(this.getActivity())
                .registerReceiver(broadcastReceiver, new IntentFilter(WatchlistItemView.WATCHLIST_ITEM_DELETED));

        shownPortfolioId = getOwnedPortfolioId(getArguments());
        detachPortfolioFetchTask();
        portfolioFetchTask = portfolioCache.getOrFetch(shownPortfolioId, createPortfolioCacheListener());
        portfolioFetchTask.execute();

        // watchlist is not yet retrieved
        if (userWatchlistPositionCache.get(currentUserId.toUserBaseKey()) == null)
        {
            launchWatchlistRetrievedMilestone();
        }
        else
        {
            display();
        }
    }

    protected void launchWatchlistRetrievedMilestone()
    {
        detachWatchlistRetrievedMilestone();
        watchlistRetrievedMilestone = new WatchlistRetrievedMilestone(currentUserId.toUserBaseKey());
        watchlistRetrievedMilestone.setOnCompleteListener(createWatchlistRetrievedMilestoneListener());
        displayProgress(true);
        watchlistRetrievedMilestone.launch();
    }

    @Override public void onPause()
    {
        super.onPause();

        LocalBroadcastManager.getInstance(this.getActivity())
                .unregisterReceiver(broadcastReceiver);
    }

    @Override public void onDestroyView()
    {
        detachWatchlistRetrievedMilestone();
        detachUserWatchlistFetchTask();
        detachPortfolioFetchTask();
        if (watchlistPortfolioHeaderView != null)
        {
            watchlistPortfolioHeaderView.setOnStateChangeListener(null);
        }
        watchlistPortfolioHeaderView = null;

        if (watchlistPositionListView != null)
        {
            SwipeListView swipeListView = watchlistPositionListView.getRefreshableView();
            if (swipeListView != null)
            {
                swipeListView.setSwipeListViewListener(null);
                swipeListView.removeCallbacks(setOffsetRunnable);
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

    protected void detachWatchlistRetrievedMilestone()
    {
        if (watchlistRetrievedMilestone != null)
        {
            watchlistRetrievedMilestone.setOnCompleteListener(null);
        }
        watchlistRetrievedMilestone = null;
    }

    protected void detachUserWatchlistFetchTask()
    {
        if (userWatchlistPositionFetchTask != null)
        {
            userWatchlistPositionFetchTask.setListener(null);
        }
        userWatchlistPositionFetchTask = null;
    }

    protected void detachPortfolioFetchTask()
    {
        if (portfolioFetchTask != null)
        {
            portfolioFetchTask.setListener(null);
        }
        portfolioFetchTask = null;
    }

    @Override public void onDestroy()
    {
        broadcastReceiver = null;
        gainLossModeListener = null;
        setOffsetRunnable = null;

        super.onDestroy();
    }

    private void display()
    {
        displayProgress(false);
        displayHeader();
        displayWatchlist(new SecurityIdList(userWatchlistPositionCache.get(currentUserId.toUserBaseKey())));
    }

    private void displayHeader()
    {
        if (watchlistPortfolioHeaderView != null)
        {
            watchlistPortfolioHeaderView.display(currentUserId.toUserBaseKey());
            watchlistPortfolioHeaderView.linkWith(shownPortfolioDTO, true);
            watchlistPortfolioHeaderView.setOnStateChangeListener(gainLossModeListener);
        }
    }

    private void displayWatchlist(SecurityIdList securityIds)
    {
        WatchlistAdapter newAdapter = createWatchlistAdapter();
        newAdapter.addAll(securityIds);
        watchlistPositionListView.setAdapter(newAdapter);
        watchListAdapter = newAdapter;
        watchlistPositionListView.onRefreshComplete();
    }

    private void openWatchlistItemEditor(int position)
    {
        // TODO discover why sometimes we would get a mismatch
        if (position < watchListAdapter.getCount())
        {
            SecurityId securityId = (SecurityId) watchListAdapter.getItem(position);
            Bundle args = new Bundle();
            if (securityId != null)
            {
                args.putBundle(WatchlistEditFragment.BUNDLE_KEY_SECURITY_ID_BUNDLE, securityId.getArgs());
                if (watchlistPositionCache.get(securityId) != null)
                {
                    args.putString(WatchlistEditFragment.BUNDLE_KEY_TITLE, getString(R.string.watchlist_edit_title));
                }
            }
            getNavigator().pushFragment(WatchlistEditFragment.class, args, Navigator.PUSH_UP_FROM_BOTTOM);
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

    @Override public boolean isTabBarVisible()
    {
        return false;
    }

    protected DTOCache.Listener<UserBaseKey, SecurityIdList> createSecurityIdListCacheListener()
    {
        return new WatchlistPositionFragmentSecurityIdListCacheListener();
    }

    protected DTOCache.Listener<OwnedPortfolioId, PortfolioDTO> createPortfolioCacheListener()
    {
        return new WatchlistPositionFragmentPortfolioCacheListener();
    }

    protected SwipeListViewListener createSwipeListViewListener()
    {
        return new WatchlistPositionFragmentSwipeListViewListener();
    }

    protected class WatchlistPositionFragmentSecurityIdListCacheListener implements DTOCache.Listener<UserBaseKey, SecurityIdList>
    {
        @Override public void onDTOReceived(UserBaseKey key, SecurityIdList value, boolean fromCache)
        {
            displayWatchlist(value);
        }

        @Override public void onErrorThrown(UserBaseKey key, Throwable error)
        {
            watchlistPositionListView.onRefreshComplete();
            THToast.show(getString(R.string.error_fetch_portfolio_watchlist));
        }
    }

    protected class RefreshWatchlisListener implements DTOCache.Listener<UserBaseKey, SecurityIdList>
    {
        @Override public void onDTOReceived(UserBaseKey key, SecurityIdList value, boolean fromCache)
        {
            if (fromCache)
            {
                return;
            }
            watchlistPositionListView.onRefreshComplete();
            displayWatchlist(value);
        }

        @Override public void onErrorThrown(UserBaseKey key, Throwable error)
        {
            watchlistPositionListView.onRefreshComplete();
            if (watchListAdapter == null || watchListAdapter.getCount() <= 0)
            {
                THToast.show(getString(R.string.error_fetch_portfolio_watchlist));
            }

        }
    }

    protected class WatchlistPositionFragmentPortfolioCacheListener implements DTOCache.Listener<OwnedPortfolioId, PortfolioDTO>
    {
        @Override public void onDTOReceived(OwnedPortfolioId key, PortfolioDTO value,
        boolean fromCache)
        {
            shownPortfolioDTO = value;
            displayHeader();
        }

        @Override public void onErrorThrown(OwnedPortfolioId key, Throwable error)
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
            localyticsSession.tagEvent(LocalyticsConstants.Watchlist_CellSwipe);
            super.onStartOpen(position, action, right);
        }

        @Override public void onDismiss(int[] reverseSortedPositions)
        {
            super.onDismiss(reverseSortedPositions);
            fetchSecurityIdList();
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
                    watchlistListViewCopy.setOffsetLeft(watchlistListViewCopy.getWidth() -
                            getResources().getDimension(R.dimen.watchlist_item_button_width)
                                    * NUMBER_OF_WATCHLIST_SWIPE_BUTTONS_BEHIND);
                }
            }
        }
    }
}
