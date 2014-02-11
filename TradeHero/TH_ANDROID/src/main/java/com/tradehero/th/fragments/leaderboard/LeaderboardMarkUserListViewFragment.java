package com.tradehero.th.fragments.leaderboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.tradehero.th.R;
import com.tradehero.th.adapters.LoaderDTOAdapter;
import com.tradehero.th.api.leaderboard.LeaderboardDTO;
import com.tradehero.th.api.leaderboard.LeaderboardUserDTO;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.fragments.tutorial.WithTutorial;
import com.tradehero.th.loaders.ListLoader;
import com.tradehero.th.utils.Constants;
import java.util.Date;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Provider;
import org.ocpsoft.prettytime.PrettyTime;

/** Created with IntelliJ IDEA. User: tho Date: 10/14/13 Time: 12:34 PM Copyright (c) TradeHero */
public class LeaderboardMarkUserListViewFragment extends BaseLeaderboardFragment
        implements SortTypeChangedListener
{
    @Inject protected Provider<PrettyTime> prettyTime;

    private LeaderboardMarkUserListAdapter leaderboardMarkUserListAdapter;
    private LeaderboardMarkUserListView leaderboardMarkUserListView;
    private TextView leaderboardMarkUserMarkingTime;

    protected LeaderboardMarkUserLoader leaderboardMarkUserLoader;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.leaderboard_listview, container, false);
        initViews(view);
        inflateHeaderView(inflater);
        return view;
    }

    protected void initViews(View view)
    {
        leaderboardMarkUserListView = (LeaderboardMarkUserListView) view.findViewById(R.id.leaderboard_listview);
    }

    //<editor-fold desc="ActionBar">
    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override protected int getMenuResource()
    {
        return R.menu.leaderboard_listview_menu;
    }

    @Override public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.leaderboard_listview_menu_help:
                getNavigator().showTutorial(this);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    //</editor-fold>

    @Override protected void setCurrentUserProfileDTO(UserProfileDTO currentUserProfileDTO)
    {
        super.setCurrentUserProfileDTO(currentUserProfileDTO);
        if (leaderboardMarkUserListAdapter != null)
        {
            leaderboardMarkUserListAdapter.setCurrentUserProfileDTO(currentUserProfileDTO);
            leaderboardMarkUserListAdapter.notifyDataSetChanged();
        }
    }

    protected void inflateHeaderView(LayoutInflater inflater)
    {
        if (leaderboardMarkUserListView != null)
        {
            View headerView = inflater.inflate(getHeaderViewResId(), null);
            if (headerView != null)
            {
                leaderboardMarkUserListView.getRefreshableView().addHeaderView(headerView, null, false);
                initHeaderView(headerView);
            }
        }
    }

    protected int getHeaderViewResId()
    {
        return R.layout.leaderboard_listview_header;
    }

    protected void initHeaderView(View headerView)
    {
        String leaderboardDefDesc = getArguments().getString(BUNDLE_KEY_LEADERBOARD_DEF_DESC);

        TextView leaderboardMarkUserTimePeriod = (TextView) headerView.findViewById(R.id.leaderboard_time_period);
        if (leaderboardMarkUserTimePeriod != null)
        {
            if (leaderboardDefDesc != null)
            {
                leaderboardMarkUserTimePeriod.setText(leaderboardDefDesc);
                leaderboardMarkUserTimePeriod.setVisibility(View.VISIBLE);
            }
            else
            {
                leaderboardMarkUserTimePeriod.setVisibility(View.GONE);
            }
        }
        leaderboardMarkUserMarkingTime = (TextView) headerView.findViewById(R.id.leaderboard_marking_time);
    }

    @Override public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        int leaderboardId = getArguments().getInt(BUNDLE_KEY_LEADERBOARD_ID);

        leaderboardMarkUserListAdapter = new LeaderboardMarkUserListAdapter(
                getActivity(), getActivity().getLayoutInflater(), leaderboardId, getCurrentSortType().getLayoutResourceId());
        leaderboardMarkUserListAdapter.setDTOLoaderCallback(new LeaderboardMarkUserListViewFragmentListLoaderCallback());
        leaderboardMarkUserListAdapter.setCurrentUserProfileDTO(currentUserProfileDTO);
        leaderboardMarkUserListAdapter.setUserInteractor(userInteractor);
        leaderboardMarkUserListView.setAdapter(leaderboardMarkUserListAdapter);
        leaderboardMarkUserListView.setOnRefreshListener(leaderboardMarkUserListAdapter);
        leaderboardMarkUserListView.setEmptyView(getView().findViewById(android.R.id.empty));

        Bundle loaderBundle = new Bundle(getArguments());
        leaderboardMarkUserLoader = (LeaderboardMarkUserLoader) getActivity().getSupportLoaderManager().initLoader(
                leaderboardId, loaderBundle, leaderboardMarkUserListAdapter.getLoaderCallback());

        // when loader is available
        setSortTypeChangeListener(this);
    }

    @Override public void onDestroyView()
    {
        if (leaderboardMarkUserListAdapter != null)
        {
            leaderboardMarkUserListAdapter.setDTOLoaderCallback(null);
        }
        leaderboardMarkUserListAdapter = null;

        if (leaderboardMarkUserListView != null)
        {
            leaderboardMarkUserListView.setOnRefreshListener((LeaderboardMarkUserListAdapter) null);
        }
        leaderboardMarkUserListView = null;
        super.onDestroyView();
    }

    @Override public void onSortTypeChange(LeaderboardSortType sortType)
    {
        leaderboardMarkUserLoader.setSortType(sortType);
        leaderboardMarkUserLoader.reload();

        //update layoutResourceId
        leaderboardMarkUserListAdapter.setLayoutResourceId(sortType.getLayoutResourceId());
        invalidateCachedItemView();
    }

    /**
     * http://grepcode.com/file/repository.grepcode.com/java/ext/com.google.android/android/4.0.3_r1/android/widget/ListView.java#443
     * this crazy way works and is the only way I found to clear ListView's recycle (reusing item view)
     */
    protected void invalidateCachedItemView()
    {
        leaderboardMarkUserListView.setAdapter(leaderboardMarkUserListAdapter);
    }

    //<editor-fold desc="BaseFragment.TabBarVisibilityInformer">
    @Override public boolean isTabBarVisible()
    {
        return false;
    }
    //</editor-fold>

    private class LeaderboardMarkUserListViewFragmentListLoaderCallback extends LoaderDTOAdapter.ListLoaderCallback<LeaderboardUserDTO>
    {
        @Override public ListLoader<LeaderboardUserDTO> onCreateLoader(Bundle args)
        {
            int leaderboardId = args.getInt(BUNDLE_KEY_LEADERBOARD_ID);
            boolean includeFoF = args.getBoolean(LeaderboardDTO.INCLUDE_FOF);
            LeaderboardMarkUserLoader leaderboardMarkUserLoader = new LeaderboardMarkUserLoader(getActivity(), leaderboardId, getCurrentSortType(), includeFoF);
            leaderboardMarkUserLoader.setPerPage(Constants.LEADERBOARD_MARK_USER_ITEM_PER_PAGE);
            return leaderboardMarkUserLoader;
        }

        @Override public void onLoadFinished(ListLoader<LeaderboardUserDTO> loader, List<LeaderboardUserDTO> data)
        {
            // display marking time
            LeaderboardMarkUserLoader leaderboardMarkUserLoader = (LeaderboardMarkUserLoader) loader;
            Date markingTime = leaderboardMarkUserLoader.getMarkUtc();
            if (markingTime != null && leaderboardMarkUserMarkingTime != null)
            {
                leaderboardMarkUserMarkingTime.setText(String.format("(%s)", prettyTime.get().format(markingTime)));
            }
            leaderboardMarkUserListView.onRefreshComplete();
        }
    }
}
