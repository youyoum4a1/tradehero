package com.tradehero.th.fragments.leaderboard;

import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import com.tradehero.th.R;
import com.tradehero.th.api.leaderboard.LeaderboardDTO;
import com.tradehero.th.api.leaderboard.LeaderboardUserDTO;
import com.tradehero.th.fragments.base.BaseFragment;
import com.tradehero.th.fragments.base.BaseListFragment;
import java.util.List;

/** Created with IntelliJ IDEA. User: tho Date: 10/14/13 Time: 12:34 PM Copyright (c) TradeHero */
public class LeaderboardListViewFragment extends BaseListFragment
        implements BaseFragment.ArgumentsChangeListener, LoaderManager.LoaderCallbacks<List<LeaderboardUserDTO>>
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
