package com.tradehero.th.fragments.leaderboard;

import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
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
import java.util.List;

/** Created with IntelliJ IDEA. User: tho Date: 10/14/13 Time: 12:34 PM Copyright (c) TradeHero */
public class LeaderboardListViewFragment extends BaseListFragment
        implements BaseFragment.ArgumentsChangeListener,
        LoaderManager.LoaderCallbacks<List<LeaderboardUserDTO>>
{
    private LeaderboardListAdapter leaderboardListAdapter;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        leaderboardListAdapter =
                new LeaderboardListAdapter(getActivity(), getActivity().getLayoutInflater(), null, R.layout.leaderboard_user_view);
        setListAdapter(leaderboardListAdapter);

        getLoaderManager().initLoader(0, getArguments(), this);
    }

    //<editor-fold desc="ActionBar">
    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.leaderboard_listview_menu, menu);

        getSherlockActivity().getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_SHOW_HOME);
        getSherlockActivity().getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.leaderboard_listview_sort:
                LeaderboardSortTypeSelectorDialog sortTypeDialog = new LeaderboardSortTypeSelectorDialog(getActivity(),
                        new LeaderboardSortTypeSelectorDialog.OnSortTypeChangedListener()
                        {
                            @Override public void onSortTypeChanged(LeaderboardSortType newSortType)
                            {

                            }
                        });
                sortTypeDialog.show();
                break;

            case R.id.leaderboard_listview_help:
                THToast.show("Not yet implemented");
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
