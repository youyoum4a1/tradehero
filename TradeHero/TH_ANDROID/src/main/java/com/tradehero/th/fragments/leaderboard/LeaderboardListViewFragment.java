package com.tradehero.th.fragments.leaderboard;

import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.leaderboard.LeaderboardDTO;
import com.tradehero.th.api.leaderboard.LeaderboardUserRankDTO;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.widget.leaderboard.LeaderboardRankingListView;
import java.util.LinkedList;
import java.util.List;

/** Created with IntelliJ IDEA. User: tho Date: 10/14/13 Time: 12:34 PM Copyright (c) TradeHero */
public class LeaderboardListViewFragment extends DashboardFragment
{
    private LeaderboardListAdapter leaderboardListAdapter;
    private LeaderboardRankingListView rankingListView;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.leaderboard_listview, container, false);
        rankingListView = (LeaderboardRankingListView) view.findViewById(android.R.id.list);
        return view;
    }

    @Override public void onResume()
    {
        leaderboardListAdapter = new LeaderboardListAdapter(getActivity(), getActivity().getLayoutInflater(), null, R.layout.leaderboard_listview_item);
        rankingListView.setAdapter(leaderboardListAdapter);
        rankingListView.setEmptyView(getView().findViewById(android.R.id.empty));
        rankingListView.setOnRefreshListener(createOnRefreshListener());

        Bundle loaderBundle = new Bundle();
        getLoaderManager().initLoader(LeaderboardLoader.UNIQUE_LOADER_ID, loaderBundle, loaderCallback);

        super.onResume();
    }

    private PullToRefreshBase.OnRefreshListener<ListView> createOnRefreshListener()
    {
        return new PullToRefreshBase.OnRefreshListener<ListView>()
        {
            @Override public void onRefresh(PullToRefreshBase<ListView> refreshView)
            {
                Loader loader = getLoaderManager().getLoader(LeaderboardLoader.UNIQUE_LOADER_ID);
                if (loader instanceof LeaderboardLoader)
                {
                    ((LeaderboardLoader) loader).loadPreviousPage();
                }
            }
        };
    }

    //<editor-fold desc="ActionBar">
    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.leaderboard_listview_menu, menu);

        ActionBar actionBar = getSherlockActivity().getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_HOME_AS_UP);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.leaderboard_listview_menu_help:
                THToast.show("Not yet implemented");
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    //</editor-fold>

    //<editor-fold desc="Loader callback">
    private LoaderManager.LoaderCallbacks<List<LeaderboardUserRankDTO>> loaderCallback = new LoaderManager.LoaderCallbacks<List<LeaderboardUserRankDTO>>()
    {
        @Override public Loader<List<LeaderboardUserRankDTO>> onCreateLoader(int id, Bundle bundle)
        {
            int leaderboardId = getArguments().getInt(LeaderboardDTO.LEADERBOARD_ID);
            LeaderboardLoader leaderboardLoader = new LeaderboardLoader(getActivity(), leaderboardId);
            leaderboardListAdapter.setLoader(leaderboardLoader);
            return leaderboardLoader;
        }

        @Override public void onLoadFinished(Loader<List<LeaderboardUserRankDTO>> loader, List<LeaderboardUserRankDTO> items)
        {
            if (leaderboardListAdapter.getCount() == 0)
            {
                leaderboardListAdapter.setUnderlyingItems(items);
            }
            leaderboardListAdapter.notifyDataSetChanged();
            rankingListView.onRefreshComplete();
        }

        @Override public void onLoaderReset(Loader<List<LeaderboardUserRankDTO>> loader)
        {
        }
    };
    //</editor-fold>

    @Override public boolean isTabBarVisible()
    {
        return false;
    }
}
