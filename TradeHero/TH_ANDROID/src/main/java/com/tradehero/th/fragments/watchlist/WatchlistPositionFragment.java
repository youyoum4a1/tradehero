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
import android.widget.ListView;
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
import com.handmark.pulltorefresh.library.PullToRefreshBase;
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
import com.tradehero.th.fragments.security.WatchlistEditFragment;
import com.tradehero.th.fragments.trending.SearchStockPeopleFragment;
import com.tradehero.th.fragments.trending.TrendingSearchType;
import com.tradehero.th.persistence.portfolio.PortfolioCache;
import com.tradehero.th.persistence.watchlist.UserWatchlistPositionCache;
import com.tradehero.th.persistence.watchlist.WatchlistPositionCache;
import com.tradehero.th.persistence.watchlist.WatchlistRetrievedMilestone;
import com.tradehero.th.utils.metrics.localytics.LocalyticsConstants;
import dagger.Lazy;
import javax.inject.Inject;

/**
 * Created with IntelliJ IDEA. User: tho Date: 1/10/14 Time: 4:11 PM Copyright (c) TradeHero
 */
public class WatchlistPositionFragment extends DashboardFragment
    implements BaseFragment.TabBarVisibilityInformer
{
    public static final String BUNDLE_KEY_SHOW_PORTFOLIO_ID_BUNDLE = WatchlistPositionFragment.class.getName() + ".showPortfolioId";
    private static final int NUMBER_OF_WATCHLIST_SWIPE_BUTTONS_BEHIND = 2;

    @InjectView(android.R.id.empty) @Optional protected ProgressBar progressBar;

    @Inject Lazy<WatchlistPositionCache> watchlistCache;
    @Inject Lazy<UserWatchlistPositionCache> userWatchlistCache;
    @Inject Lazy<PortfolioCache> portfolioCache;
    @Inject Lazy<PortfolioHeaderFactory> headerFactory;
    @Inject CurrentUserId currentUserId;
    @Inject LocalyticsSession localyticsSession;

    private DTOCache.Listener<UserBaseKey, SecurityIdList> watchlistFetchCompleteListener;
    private DTOCache.GetOrFetchTask<UserBaseKey, SecurityIdList> refreshWatchlistFetchTask;

    private DTOCache.Listener<OwnedPortfolioId, PortfolioDTO> portfolioFetchCompleteListener;
    private DTOCache.GetOrFetchTask<OwnedPortfolioId, PortfolioDTO> portfolioFetchTask;

    @InjectView(android.R.id.list) SwipeListView watchlistListView;
    @InjectView(R.id.watchlist_position_list_header) WatchlistPortfolioHeaderView watchlistPortfolioHeaderView;
    @InjectView(R.id.pull_to_refresh_watchlist_listview) WatchlistPositionListView watchlistPositionListView;

    private WatchlistAdapter watchListAdapter;

    private WatchlistRetrievedMilestone watchlistRetrievedMilestone;
    private Milestone.OnCompleteListener watchlistRetrievedMilestoneListener;
    private TwoStateView.OnStateChange gainLossModeListener;
    private BroadcastReceiver broadcastReceiver;

    private OwnedPortfolioId shownPortfolioId;
    private PortfolioDTO shownPortfolioDTO;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        createWatchlistRetrievedMilestoneListener();
        createGainLossModeListener();
        createBroadcastReceiver();
        createWatchlistFetchCompleteListener();
        createPortfolioFetchListener();
    }

    protected void createWatchlistRetrievedMilestoneListener()
    {
        watchlistRetrievedMilestoneListener = new Milestone.OnCompleteListener()
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

    protected void createGainLossModeListener()
    {
        gainLossModeListener = new TwoStateView.OnStateChange()
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

    protected void createBroadcastReceiver()
    {
        broadcastReceiver = new BroadcastReceiver()
        {
            @Override public void onReceive(Context context, Intent intent)
            {
                if (watchlistListView != null)
                {
                    int deletedItemId = intent.getIntExtra(WatchlistItemView.BUNDLE_KEY_WATCHLIST_ITEM_INDEX, -1);
                    if (deletedItemId != -1)
                    {
                        localyticsSession.tagEvent(LocalyticsConstants.Watchlist_Delete);
                        watchlistListView.dismiss(deletedItemId);
                        watchlistListView.closeOpenedItems();
                    }
                }
            }
        };
    }

    protected void createWatchlistFetchCompleteListener()
    {
        watchlistFetchCompleteListener = new DTOCache.Listener<UserBaseKey, SecurityIdList>()
        {
            @Override public void onDTOReceived(UserBaseKey key, SecurityIdList value, boolean fromCache)
            {
                watchlistPositionListView.onRefreshComplete();
                watchListAdapter.setItems(userWatchlistCache.get().get(currentUserId.toUserBaseKey()));
                watchListAdapter.notifyDataSetChanged();
            }

            @Override public void onErrorThrown(UserBaseKey key, Throwable error)
            {
                watchlistPositionListView.onRefreshComplete();
                THToast.show(getString(R.string.error_fetch_portfolio_watchlist));
            }
        };
    }

    protected void createPortfolioFetchListener()
    {
        portfolioFetchCompleteListener = new DTOCache.Listener<OwnedPortfolioId, PortfolioDTO>()
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
        };
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
            watchlistListView.post(new Runnable()
            {
                @Override public void run()
                {
                    SwipeListView watchlistListViewCopy = watchlistListView;
                    if (watchlistListViewCopy != null)
                    {
                        watchlistListViewCopy.setOffsetLeft(watchlistListViewCopy.getWidth() -
                                getResources().getDimension(R.dimen.watchlist_item_button_width) * NUMBER_OF_WATCHLIST_SWIPE_BUTTONS_BEHIND);
                    }
                }
            });
            watchlistListView.setEmptyView(view.findViewById(R.id.watchlist_position_list_empty_view));

            initPullToRefreshListView(view);
        }
    }

    private void initPullToRefreshListView(View view)
    {
        ((ViewGroup) view).removeView(watchlistListView);
        watchlistPositionListView.setRefreshableView(watchlistListView);
        watchlistPositionListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>()
        {
            @Override public void onRefresh(PullToRefreshBase<ListView> refreshView)
            {
                detachWatchlistCacheTask();
                refreshWatchlistFetchTask = userWatchlistCache.get().getOrFetch(currentUserId.toUserBaseKey(), true, watchlistFetchCompleteListener);
                refreshWatchlistFetchTask.execute();
            }
        });
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
                    bundle.putString(SearchStockPeopleFragment.BUNDLE_KEY_RESTRICT_SEARCH_TYPE, TrendingSearchType.STOCKS.name());
                    getNavigator().pushFragment(SearchStockPeopleFragment.class, bundle);
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

        shownPortfolioId = new OwnedPortfolioId(getArguments().getBundle(BUNDLE_KEY_SHOW_PORTFOLIO_ID_BUNDLE));
        detachPortfolioFetchTask();
        portfolioFetchTask = portfolioCache.get().getOrFetch(shownPortfolioId, portfolioFetchCompleteListener);
        portfolioFetchTask.execute();

        // watchlist is not yet retrieved
        if (userWatchlistCache.get().get(currentUserId.toUserBaseKey()) == null)
        {
            detachWatchlistRetrievedMilestone();
            watchlistRetrievedMilestone = new WatchlistRetrievedMilestone(currentUserId.toUserBaseKey());
            watchlistRetrievedMilestone.setOnCompleteListener(watchlistRetrievedMilestoneListener);
            watchlistRetrievedMilestone.launch();
            displayProgress(true);
        }
        else
        {
            display();
        }
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
        detachWatchlistCacheTask();
        detachPortfolioFetchTask();
        if (watchlistPortfolioHeaderView != null)
        {
            watchlistPortfolioHeaderView.setOnStateChangeListener(null);
        }
        watchlistPortfolioHeaderView = null;

        if (watchlistListView != null)
        {
            watchlistListView.setSwipeListViewListener(null);
        }
        watchlistListView = null;

        watchListAdapter = null;

        if (watchlistPositionListView != null)
        {
            watchlistPositionListView.onRefreshComplete();
            watchlistPositionListView.setOnRefreshListener(
                    (PullToRefreshBase.OnRefreshListener<ListView>) null);
        }

        super.onDestroyView();
    }

    protected void detachWatchlistCacheTask()
    {
        if (refreshWatchlistFetchTask != null)
        {
            refreshWatchlistFetchTask.setListener(null);
        }
        refreshWatchlistFetchTask = null;
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
        portfolioFetchCompleteListener = null;
        watchlistFetchCompleteListener = null;
        broadcastReceiver = null;
        gainLossModeListener = null;
        watchlistRetrievedMilestoneListener = null;

        super.onDestroy();
    }

    protected void detachWatchlistRetrievedMilestone()
    {
        if (watchlistRetrievedMilestone != null)
        {
            watchlistRetrievedMilestone.setOnCompleteListener(null);
        }
        watchlistRetrievedMilestone = null;
    }

    private void display()
    {
        displayProgress(false);
        displayHeader();
        displayWatchlist();
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

    private void displayWatchlist()
    {
        watchListAdapter = createWatchlistAdapter();
        watchListAdapter.setItems(userWatchlistCache.get().get(currentUserId.toUserBaseKey()));
        watchlistListView.setAdapter(watchListAdapter);
        watchlistListView.setSwipeListViewListener(new BaseSwipeListViewListener()
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
                if (watchListAdapter != null)
                {
                    watchListAdapter.setItems(userWatchlistCache.get().get(currentUserId.toUserBaseKey()));
                    watchListAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void openWatchlistItemEditor(int position)
    {
        SecurityId securityId = (SecurityId) watchListAdapter.getItem(position);
        Bundle args = new Bundle();
        if (securityId != null)
        {
            args.putBundle(WatchlistEditFragment.BUNDLE_KEY_SECURITY_ID_BUNDLE, securityId.getArgs());
            if (watchlistCache.get().get(securityId) != null)
            {
                args.putString(WatchlistEditFragment.BUNDLE_KEY_TITLE, getString(R.string.watchlist_edit_title));
            }
        }
        getNavigator().pushFragment(WatchlistEditFragment.class, args, Navigator.PUSH_UP_FROM_BOTTOM);
    }

    private WatchlistAdapter createWatchlistAdapter()
    {
        return new WatchlistAdapter(getActivity(), getActivity().getLayoutInflater(), R.layout.watchlist_item_view);
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
}
