package com.tradehero.th.fragments.position;

import com.tradehero.th.api.leaderboard.LeaderboardDefDTO;
import com.tradehero.th.api.leaderboard.LeaderboardUserDTO;
import timber.log.Timber;

public class LeaderboardPositionListFragment
        extends PositionListFragment
{
    private boolean timeRestricted;

    @Override protected void createPositionItemAdapter()
    {
        timeRestricted =
                getArguments().getBoolean(LeaderboardDefDTO.LEADERBOARD_DEF_TIME_RESTRICTED, false);
        if (positionItemAdapter != null)
        {
            positionItemAdapter.setCellListener(null);
        }
        positionItemAdapter = new LeaderboardPositionItemAdapter(
                getActivity(),
                getActivity().getLayoutInflater(),
                getLayoutResIds(),
                timeRestricted);
        positionItemAdapter.setCellListener(this);
    }

    @Override public void onResume()
    {
        String periodStart =
                getArguments().getString(LeaderboardUserDTO.LEADERBOARD_PERIOD_START_STRING);
        Timber.d("Period Start: %s" + periodStart);

        super.onResume();
    }
}
