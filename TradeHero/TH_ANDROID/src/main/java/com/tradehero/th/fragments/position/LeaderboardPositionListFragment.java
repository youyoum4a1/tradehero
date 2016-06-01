package com.ayondo.academy.fragments.position;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import javax.inject.Inject;

public class LeaderboardPositionListFragment
        extends PositionListFragment
{
    @SuppressWarnings("unused") @Inject Context doNotRemoveOrItFails;

    private static final String LEADERBOARD_DEF_TIME_RESTRICTED = "LEADERBOARD_DEF_TIME_RESTRICTED";
    private static final String LEADERBOARD_PERIOD_START_STRING = "LEADERBOARD_PERIOD_START_STRING";

    private static final boolean DEFAULT_IS_TIME_RESTRICTED = false;

    private boolean isTimeRestricted;

    public static void putLeaderboardTimeRestricted(@NonNull Bundle args, boolean isTimeRestricted)
    {
        args.putBoolean(LEADERBOARD_DEF_TIME_RESTRICTED, isTimeRestricted);
    }

    public static boolean getLeaderBoardTimeRestricted(@NonNull Bundle args)
    {
        return args.getBoolean(LEADERBOARD_DEF_TIME_RESTRICTED, DEFAULT_IS_TIME_RESTRICTED);
    }

    public static void putLeaderboardPeriodStartString(@NonNull Bundle args,@NonNull String periodStartString)
    {
        args.putString(LEADERBOARD_PERIOD_START_STRING, periodStartString);
    }

    @Nullable public static String getLeaderboardPeriodStartString(@NonNull Bundle args)
    {
        return args.getString(LEADERBOARD_PERIOD_START_STRING);
    }

    @Override protected PositionItemAdapter createPositionItemAdapter()
    {
        isTimeRestricted = getLeaderBoardTimeRestricted(getArguments());
        return super.createPositionItemAdapter();
    }

    @Override public void onResume()
    {
        String periodStart = getLeaderboardPeriodStartString(getArguments());
        //Timber.d("Period Start: %s" + periodStart);

        super.onResume();
    }
}
