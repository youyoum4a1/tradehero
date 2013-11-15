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
import java.util.Date;
import java.util.List;
import javax.inject.Inject;
import org.ocpsoft.prettytime.PrettyTime;

/** Created with IntelliJ IDEA. User: tho Date: 10/14/13 Time: 12:34 PM Copyright (c) TradeHero */
public class LeaderboardMarkUserListViewFragment extends BaseLeaderboardFragment
        implements SortTypeChangedListener
{
    public static final String TITLE = LeaderboardMarkUserListViewFragment.class.getName() + ".title";

    @Inject protected PrettyTime prettyTime;

    private LeaderboardMarkUserListAdapter leaderboardMarkUserListAdapter;
    private LeaderboardMarkUserListView leaderboardMarkUserListView;
    private TextView leaderboardMarkUserMarkingTime;
    private LeaderboardMarkUserLoader leaderboardMarkUserLoader;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.leaderboard_listview, container, false);

        leaderboardMarkUserListView = (LeaderboardMarkUserListView) view.findViewById(R.id.leaderboard_listview);
        leaderboardMarkUserListAdapter = new LeaderboardMarkUserListAdapter(
                getActivity(), getActivity().getLayoutInflater(), null, getCurrentSortType().getLayoutResourceId());
        leaderboardMarkUserListView.setAdapter(leaderboardMarkUserListAdapter);
        leaderboardMarkUserListView.setOnRefreshListener(createOnRefreshListener());
        leaderboardMarkUserListView.setEmptyView(view.findViewById(android.R.id.empty));

        View headerView = inflater.inflate(R.layout.leaderboard_listview_header, null);
        leaderboardMarkUserListView.getRefreshableView().addHeaderView(headerView, null, false);

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

        Bundle loaderBundle = new Bundle(getArguments());
        leaderboardMarkUserLoader = (LeaderboardMarkUserLoader) getLoaderManager()
                .initLoader(LeaderboardMarkUserLoader.UNIQUE_LOADER_ID, loaderBundle, loaderCallback);

        setSortTypeChangeListener(this);
        leaderboardMarkUserListAdapter.setLoader(leaderboardMarkUserLoader);
    }

    @Override public void onSortTypeChange(LeaderboardSortType sortType)
    {
        leaderboardMarkUserLoader.setSortType(sortType);
        leaderboardMarkUserLoader.reload();

        // update layoutResourceId
        // http://grepcode.com/file/repository.grepcode.com/java/ext/com.google.android/android/4.0.3_r1/android/widget/ListView.java#443
        // this crazy way is the only way I found to clear ListView's recycle (reusing item view)
        leaderboardMarkUserListAdapter.setLayoutResourceId(sortType.getLayoutResourceId());
        leaderboardMarkUserListView.setAdapter(leaderboardMarkUserListAdapter);
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
            int leaderboardId = bundle.getInt(LeaderboardDTO.LEADERBOARD_ID);
            LeaderboardSortType currentSortType = getCurrentSortType();
            return new LeaderboardMarkUserLoader(getActivity(), leaderboardId, currentSortType);
        }

        @Override public void onLoadFinished(Loader<List<LeaderboardUserDTO>> loader, List<LeaderboardUserDTO> items)
        {
            // modify data set
            if (leaderboardMarkUserListAdapter.getCount() == 0)
            {
                leaderboardMarkUserListAdapter.setUnderlyingItems(items);
            }

            // display marking time
            LeaderboardMarkUserLoader leaderboardMarkUserLoader = (LeaderboardMarkUserLoader) loader;
            Date markingTime = leaderboardMarkUserLoader.getMarkUtc();
            if (markingTime != null && leaderboardMarkUserMarkingTime != null)
            {
                leaderboardMarkUserMarkingTime.setText(prettyTime.format(markingTime));
            }

            leaderboardMarkUserListAdapter.notifyDataSetChanged();
            leaderboardMarkUserListView.onRefreshComplete();
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
