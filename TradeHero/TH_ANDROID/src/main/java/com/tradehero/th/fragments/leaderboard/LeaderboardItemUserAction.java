package com.tradehero.th.fragments.leaderboard;

import android.support.annotation.NonNull;

public class LeaderboardItemUserAction
{
    public enum UserActionType
    {
        PROFILE, POSITIONS, FOLLOW, UNFOLLOW, RULES
    }

    @NonNull public final LeaderboardItemDisplayDTO dto;
    @NonNull public final UserActionType actionType;

    public LeaderboardItemUserAction(
            @NonNull LeaderboardItemDisplayDTO dto,
            @NonNull UserActionType actionType)
    {
        this.dto = dto;
        this.actionType = actionType;
    }
}