package com.tradehero.th.fragments.position;

import android.os.Bundle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import timber.log.Timber;

public class LeaderboardPositionListFragment
        extends PositionListFragment
{
    private static final String LEADERBOARD_DEF_TIME_RESTRICTED = "LEADERBOARD_DEF_TIME_RESTRICTED";
    private static final String LEADERBOARD_PERIOD_START_STRING = "LEADERBOARD_PERIOD_START_STRING";

    private static final boolean DEFAULT_IS_TIME_RESTRICTED = false;

    private boolean isTimeRestricted;

    public static void putLeaderboardTimeRestricted(@NotNull Bundle args, boolean isTimeRestricted)
    {
        args.putBoolean(LEADERBOARD_DEF_TIME_RESTRICTED, isTimeRestricted);
    }

    public static boolean getLeaderBoardTimeRestricted(@NotNull Bundle args)
    {
        return args.getBoolean(LEADERBOARD_DEF_TIME_RESTRICTED, DEFAULT_IS_TIME_RESTRICTED);
    }

    public static void putLeaderboardPeriodStartString(@NotNull Bundle args,@NotNull String periodStartString)
    {
        args.putString(LEADERBOARD_PERIOD_START_STRING, periodStartString);
    }

    @Nullable public static String getLeaderboardPeriodStartString(@NotNull Bundle args)
    {
        return args.getString(LEADERBOARD_PERIOD_START_STRING);
    }

    @Override protected void createPositionItemAdapter()
    {
        isTimeRestricted = getLeaderBoardTimeRestricted(getArguments());

        if (positionItemAdapter != null)
        {
            positionItemAdapter.setCellListener(null);
        }
        positionItemAdapter = new LeaderboardPositionItemAdapter(
                getActivity(),
                getLayoutResIds(),
                isTimeRestricted);
        positionItemAdapter.setCellListener(this);
    }

    @Override public void onResume()
    {
        String periodStart = getLeaderboardPeriodStartString(getArguments());
        //Timber.d("Period Start: %s" + periodStart);

        super.onResume();
    }
}
