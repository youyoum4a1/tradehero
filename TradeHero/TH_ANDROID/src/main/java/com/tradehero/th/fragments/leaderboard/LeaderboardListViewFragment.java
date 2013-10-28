package com.tradehero.th.fragments.leaderboard;

import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.leaderboard.LeaderboardDTO;
import com.tradehero.th.api.leaderboard.LeaderboardUserDTO;
import com.tradehero.th.fragments.base.BaseFragment;
import com.tradehero.th.fragments.base.BaseListFragment;
import com.tradehero.th.fragments.base.DashboardListFragment;
import java.util.List;

/** Created with IntelliJ IDEA. User: tho Date: 10/14/13 Time: 12:34 PM Copyright (c) TradeHero */
public class LeaderboardListViewFragment extends DashboardListFragment
        implements BaseFragment.ArgumentsChangeListener,
        LoaderManager.LoaderCallbacks<List<LeaderboardUserDTO>>
{
    private LeaderboardListAdapter leaderboardListAdapter;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getLoaderManager().initLoader(0, getArguments(), this);
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.leaderboard_listview, container, false);
        return view;
    }

    @Override public void onResume()
    {
        leaderboardListAdapter = new LeaderboardListAdapter(getActivity(), getActivity().getLayoutInflater(), null, R.layout.leaderboard_user_view);
        setListAdapter(leaderboardListAdapter);

        getListView().setEmptyView(getView().findViewById(android.R.id.empty));
        super.onResume();
    }

    //<editor-fold desc="ActionBar">
    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.leaderboard_listview_menu, menu);

        ActionBar actionBar = getSherlockActivity().getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_SHOW_HOME);
        actionBar.setDisplayHomeAsUpEnabled(true);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            //case R.id.leaderboard_listview_sort:
            //    LeaderboardSortTypeSelectorDialog sortTypeDialog = new LeaderboardSortTypeSelectorDialog(getActivity(),
            //            new LeaderboardSortTypeSelectorDialog.OnSortTypeChangedListener()
            //            {
            //                @Override public void onSortTypeChanged(LeaderboardSortType newSortType)
            //                {
            //
            //                }
            //            });
            //    sortTypeDialog.show();
            //    break;

            case R.id.leaderboard_listview_help:
                THToast.show("Not yet implemented");
                break;

            case android.R.id.home:
                getNavigator().popFragment();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    //</editor-fold>


    @Override public Loader<List<LeaderboardUserDTO>> onCreateLoader(int id, Bundle bundle)
    {
        int leaderboardId = getArguments().getInt(LeaderboardDTO.LEADERBOARD_ID);
        return new LeaderboardLoader(getActivity(), leaderboardId);
    }

    @Override public void onLoadFinished(Loader<List<LeaderboardUserDTO>> loader, List<LeaderboardUserDTO> items)
    {
        leaderboardListAdapter.setItems(items);
        leaderboardListAdapter.notifyDataSetChanged();
    }

    @Override public void onLoaderReset(Loader<List<LeaderboardUserDTO>> loader)
    {
    }

    @Override public void onArgumentsChanged(Bundle args)
    {
        //getLoaderManager().initLoader(0, args, this);
    }
}
