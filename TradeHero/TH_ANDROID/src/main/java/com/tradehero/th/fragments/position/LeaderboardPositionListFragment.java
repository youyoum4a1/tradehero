package com.tradehero.th.fragments.position;

import android.os.Bundle;
import com.tradehero.th.api.leaderboard.LeaderboardDefDTO;
import com.tradehero.th.api.leaderboard.LeaderboardUserDTO;
import com.tradehero.th.api.position.PositionDTO;
import com.tradehero.th.api.position.PositionInPeriodDTO;
import com.tradehero.th.fragments.trade.TradeListFragment;
import com.tradehero.th.fragments.trade.TradeListInPeriodFragment;
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

    @Override public void onTradeHistoryClicked(PositionDTO clickedPositionDTO)
    {
        // We should not call the super method.
        Bundle args = new Bundle();
        if (clickedPositionDTO instanceof PositionInPeriodDTO)
        {
            args.putBundle(
                    TradeListInPeriodFragment.BUNDLE_KEY_OWNED_LEADERBOARD_POSITION_ID_BUNDLE,
                    clickedPositionDTO.getPositionDTOKey().getArgs());
            getDashboardNavigator().pushFragment(TradeListInPeriodFragment.class, args);
        }
        else
        {
            args.putBundle(TradeListFragment.BUNDLE_KEY_OWNED_POSITION_ID_BUNDLE,
                    clickedPositionDTO.getPositionDTOKey().getArgs());
            getDashboardNavigator().pushFragment(TradeListFragment.class, args);
        }
    }
}
