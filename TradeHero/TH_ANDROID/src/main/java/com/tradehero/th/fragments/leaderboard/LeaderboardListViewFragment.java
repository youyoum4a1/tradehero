package com.tradehero.th.fragments.leaderboard;

import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.leaderboard.LeaderboardDTO;
import com.tradehero.th.api.leaderboard.LeaderboardDefDTO;
import com.tradehero.th.api.leaderboard.LeaderboardUserDTO;
import com.tradehero.th.widget.leaderboard.LeaderboardMarkUserListView;
import java.util.Date;
import java.util.List;
import javax.inject.Inject;
import org.ocpsoft.prettytime.PrettyTime;

/** Created with IntelliJ IDEA. User: tho Date: 10/14/13 Time: 12:34 PM Copyright (c) TradeHero */
public class LeaderboardListViewFragment extends AbstractLeaderboardFragment
{
    public static final String TITLE = LeaderboardListViewFragment.class.getName() + ".title";

    @Inject protected PrettyTime prettyTime;

    private LeaderboardMarkUserListAdapter leaderboardMarkUserListAdapter;
    private LeaderboardMarkUserListView leaderboardMarkUserListView;
    private TextView leaderboardMarkUserMarkingTime;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.leaderboard_listview, container, false);

        leaderboardMarkUserListView = (LeaderboardMarkUserListView) view.findViewById(R.id.leaderboard_listview);
        leaderboardMarkUserListAdapter = new LeaderboardMarkUserListAdapter(
                getActivity(), getActivity().getLayoutInflater(), null, R.layout.leaderboard_listview_item);
        leaderboardMarkUserListView.setAdapter(leaderboardMarkUserListAdapter);
        leaderboardMarkUserListView.setOnRefreshListener(createOnRefreshListener());
        leaderboardMarkUserListView.setEmptyView(view.findViewById(android.R.id.empty));

        View headerView = inflater.inflate(R.layout.leaderboard_listview_header, null);
        leaderboardMarkUserListView.getRefreshableView().addHeaderView(headerView);

        initHeaderView(headerView);

        return view;
    }

    private void initHeaderView(View headerView)
    {
        String leaderboardDefDesc = getArguments().getString(LeaderboardDefDTO.LEADERBOARD_DEF_DESC);

        TextView leaderboardMarkUserTimePeriod = (TextView) headerView.findViewById(R.id.leaderboard_time_period);
        if (leaderboardDefDesc != null)
        {
            leaderboardMarkUserTimePeriod.setText(leaderboardDefDesc);
            leaderboardMarkUserTimePeriod.setVisibility(View.VISIBLE);
        }
        else
        {
            leaderboardMarkUserTimePeriod.setVisibility(View.GONE);
        }
        leaderboardMarkUserMarkingTime = (TextView) headerView.findViewById(R.id.leaderboard_marking_time);
    }

    @Override public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        Bundle loaderBundle = new Bundle();
        LeaderboardMarkUserLoader loader = (LeaderboardMarkUserLoader) getLoaderManager()
                .initLoader(LeaderboardMarkUserLoader.UNIQUE_LOADER_ID, loaderBundle, loaderCallback);

        setSortTypeChangeListener(loader);
        leaderboardMarkUserListAdapter.setLoader(loader);
    }

    private PullToRefreshBase.OnRefreshListener<ListView> createOnRefreshListener()
    {
        return new PullToRefreshBase.OnRefreshListener<ListView>()
        {
            @Override public void onRefresh(PullToRefreshBase<ListView> refreshView)
            {
                Loader loader = getLoaderManager().getLoader(LeaderboardMarkUserLoader.UNIQUE_LOADER_ID);
                if (loader instanceof LeaderboardMarkUserLoader)
                {
                    ((LeaderboardMarkUserLoader) loader).loadPreviousPage();
                }
            }
        };
    }

    //<editor-fold desc="ActionBar">
    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.leaderboard_listview_menu, menu);

        ActionBar actionBar = getSherlockActivity().getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_HOME_AS_UP);

        Bundle argument = getArguments();
        if (argument != null)
        {
            String title = argument.getString(TITLE);
            actionBar.setTitle(title == null ? "" : title);
        }
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
    private LoaderManager.LoaderCallbacks<List<LeaderboardUserDTO>> loaderCallback = new LoaderManager.LoaderCallbacks<List<LeaderboardUserDTO>>()
    {
        @Override public Loader<List<LeaderboardUserDTO>> onCreateLoader(int id, Bundle bundle)
        {
            int leaderboardId = getArguments().getInt(LeaderboardDTO.LEADERBOARD_ID);
            return new LeaderboardMarkUserLoader(getActivity(), leaderboardId);
        }

        @Override public void onLoadFinished(Loader<List<LeaderboardUserDTO>> loader, List<LeaderboardUserDTO> items)
        {
            // modify data set
            if (leaderboardMarkUserListAdapter.getCount() == 0)
            {
                leaderboardMarkUserListAdapter.setUnderlyingItems(items);
            }
            leaderboardMarkUserListAdapter.notifyDataSetChanged();
            leaderboardMarkUserListView.onRefreshComplete();

            // display marking time
            LeaderboardMarkUserLoader leaderboardMarkUserLoader = (LeaderboardMarkUserLoader) loader;
            Date markingTime = leaderboardMarkUserLoader.getMarkUtc();
            if (markingTime != null && leaderboardMarkUserMarkingTime != null)
            {
                leaderboardMarkUserMarkingTime.setText(prettyTime.format(markingTime));
            }
        }

        @Override public void onLoaderReset(Loader<List<LeaderboardUserDTO>> loader)
        {
            // TODO what should do when loader is reset
        }
    };
    //</editor-fold>

    @Override public boolean isTabBarVisible()
    {
        return false;
    }
}
