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
import com.tradehero.common.milestone.Milestone;
import com.tradehero.common.persistence.DTOCache;
import com.tradehero.common.utils.THToast;
import com.tradehero.common.widget.TwoStateView;
import com.tradehero.th.R;
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
import com.tradehero.th.persistence.watchlist.UserWatchlistPositionCache;
import com.tradehero.th.persistence.watchlist.WatchlistPositionCache;
import com.tradehero.th.persistence.watchlist.WatchlistRetrievedMilestone;
import dagger.Lazy;
import javax.inject.Inject;

/**
 * Created with IntelliJ IDEA. User: tho Date: 1/10/14 Time: 4:11 PM Copyright (c) TradeHero
 */
public class WatchlistPositionFragment extends DashboardFragment
    implements BaseFragment.TabBarVisibilityInformer
{
    private static final String TAG = WatchlistPositionFragment.class.getSimpleName();
    public static final String BUNDLE_KEY_SHOW_PORTFOLIO_ID_BUNDLE = WatchlistPositionFragment.class.getName() + ".showPortfolioId";
    private static final int NUMBER_OF_WATCHLIST_SWIPE_BUTTONS_BEHIND = 2;

    @InjectView(android.R.id.empty) @Optional protected ProgressBar progressBar;

    @Inject protected Lazy<WatchlistPositionCache> watchlistCache;
    @Inject protected Lazy<UserWatchlistPositionCache> userWatchlistCache;
    private DTOCache.Listener<UserBaseKey, SecurityIdList> watchlistFetchCompleteListener;
    private DTOCache.GetOrFetchTask<UserBaseKey, SecurityIdList> refreshWatchlistCache;
    @Inject protected Lazy<PortfolioHeaderFactory> headerFactory;
    @Inject protected CurrentUserId currentUserId;

    @InjectView(android.R.id.list) protected SwipeListView watchlistListView;
    @InjectView(R.id.watchlist_position_list_header) protected WatchlistPortfolioHeaderView watchlistPortfolioHeaderView;
    private WatchlistAdapter watchListAdapter;
    @InjectView(R.id.pull_to_refresh_watchlist_listview) protected WatchlistPositionListView watchlistPositionListView;

    private WatchlistRetrievedMilestone watchlistRetrievedMilestone;
    private Milestone.OnCompleteListener watchlistRetrievedMilestoneListener;
    private TwoStateView.OnStateChange gainLossModeListener;
    private BroadcastReceiver broadcastReceiver;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        createWatchlistRetrievedMilestoneListener();
        createGainLossModeListener();
        createBroadcastReceiver();
        createWatchlistFetchCompleteListener();
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
                    watchlistListView.setOffsetLeft(watchlistListView.getWidth() -
                            getResources().getDimension(R.dimen.watchlist_item_button_width) * NUMBER_OF_WATCHLIST_SWIPE_BUTTONS_BEHIND);
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
                refreshWatchlistCache = userWatchlistCache.get().getOrFetch(currentUserId.toUserBaseKey(), true, watchlistFetchCompleteListener);
                refreshWatchlistCache.execute();
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
                    bundle.putString(SearchStockPeopleFragment.BUNDLE_KEY_CALLER_FRAGMENT, WatchlistPositionFragment.class.getName());
                    getNavigator().pushFragment(SearchStockPeopleFragment.class, bundle);
                }
            });
        }
    }
    //</editor-fold>

    @Override public void onResume()
    {
        super.onResume();

        LocalBroadcastManager.getInstance(this.getActivity())
                .registerReceiver(broadcastReceiver, new IntentFilter(WatchlistItemView.WATCHLIST_ITEM_DELETED));

        // watchlist is not yet retrieved
        if (userWatchlistCache.get().get(currentUserId.toUserBaseKey()) == null)
        {
            detachWatchlistRetrievedMilestone();
            watchlistRetrievedMilestone = new WatchlistRetrievedMilestone(currentUserId.toUserBaseKey());
            watchlistRetrievedMilestone.setOnCompleteListener(watchlistRetrievedMilestoneListener);

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

        super.onDestroyView();
    }

    protected void detachWatchlistCacheTask()
    {
        if (refreshWatchlistCache != null)
        {
            refreshWatchlistCache.setListener(null);
        }
        refreshWatchlistCache = null;
    }

    @Override public void onDestroy()
    {
        super.onDestroy();

        watchlistFetchCompleteListener = null;
        broadcastReceiver = null;
        gainLossModeListener = null;
        watchlistRetrievedMilestoneListener = null;
        watchlistPositionListView.onRefreshComplete();
        watchlistPositionListView.setOnRefreshListener((PullToRefreshBase.OnRefreshListener<ListView>) null);
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
