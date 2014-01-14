package com.tradehero.th.fragments.watchlist;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.tradehero.common.milestone.Milestone;
import com.tradehero.th.R;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.users.CurrentUserBaseKeyHolder;
import com.tradehero.th.base.Navigator;
import com.tradehero.th.fragments.base.BaseFragment;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.fragments.portfolio.header.PortfolioHeaderFactory;
import com.tradehero.th.fragments.position.PositionListFragment;
import com.tradehero.th.fragments.security.WatchListFragment;
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
    private static final String TAG = WatchlistPositionFragment.class.getName();
    private ProgressBar progressBar;

    @Inject protected Lazy<WatchlistPositionCache> watchlistCache;
    @Inject protected Lazy<UserWatchlistPositionCache> userWatchlistCache;
    @Inject protected Lazy<PortfolioHeaderFactory> headerFactory;
    @Inject protected CurrentUserBaseKeyHolder currentUserBaseKeyHolder;

    private ListView watchlistListView;
    private WatchlistPortfolioHeaderView watchlistPortfolioHeaderView;

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
            progressBar = (ProgressBar) view.findViewById(android.R.id.empty);
            watchlistListView = (ListView) view.findViewById(android.R.id.list);

            // portfolio header
            watchlistPortfolioHeaderView = (WatchlistPortfolioHeaderView) view.findViewById(R.id.watchlist_position_list_header);
        }
    }

    @Override public void onResume()
    {
        super.onResume();

        // watchlist is not yet retrieved
        if (userWatchlistCache.get().get(currentUserBaseKeyHolder.getCurrentUserBaseKey()) == null)
        {
            WatchlistRetrievedMilestone watchlistRetrievedMilestone = new WatchlistRetrievedMilestone(currentUserBaseKeyHolder.getCurrentUserBaseKey());
            watchlistRetrievedMilestone.setOnCompleteListener(watchlistRetrievedMilestoneListener);

            displayProgress(true);
        }
        else
        {
            display();
        }
    }

    //<editor-fold desc="ActionBar Menu Actions">
    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        ActionBar actionBar = getSherlockActivity().getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);
        actionBar.setTitle(getString(R.string.watchlist));
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.position_watchlist_menu, menu);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.position_watchlist_add:
                getNavigator().pushFragment(SearchStockPeopleFragment.class, null);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    //</editor-fold>

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
            watchlistPortfolioHeaderView.display(currentUserBaseKeyHolder.getCurrentUserBaseKey());
        }
    }

    private void displayWatchlist()
    {
        WatchlistAdapter watchListAdapter = createWatchlistAdapter();
        watchListAdapter.setItems(userWatchlistCache.get().get(currentUserBaseKeyHolder.getCurrentUserBaseKey()));
        watchlistListView.setAdapter(watchListAdapter);
        watchlistListView.setOnItemClickListener(watchlistItemClickListener);
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

    private Milestone.OnCompleteListener watchlistRetrievedMilestoneListener = new Milestone.OnCompleteListener()
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


    private AdapterView.OnItemClickListener watchlistItemClickListener = new AdapterView.OnItemClickListener()
    {
        @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id)
        {
            SecurityId securityId = (SecurityId) parent.getItemAtPosition(position);
            Bundle args = new Bundle();
            if (securityId != null)
            {
                args.putBundle(WatchListFragment.BUNDLE_KEY_SECURITY_ID_BUNDLE, securityId.getArgs());
                if (watchlistCache.get().get(securityId) != null)
                {
                    args.putString(WatchListFragment.BUNDLE_KEY_TITLE, getString(R.string.edit_in_watch_list));
                }
            }
            getNavigator().pushFragment(WatchListFragment.class, args, Navigator.PUSH_UP_FROM_BOTTOM);
        }
    };
}
