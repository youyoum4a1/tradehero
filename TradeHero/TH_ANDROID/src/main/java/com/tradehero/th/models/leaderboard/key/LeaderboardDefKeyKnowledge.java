package com.tradehero.th.models.leaderboard.key;

import com.tradehero.th.api.leaderboard.key.LeaderboardDefKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.inject.Inject;

public class LeaderboardDefKeyKnowledge
{
    // For fake leaderboard definition, hardcoded on client side
    public static final int FOLLOWER_ID = -5;
    public static final int HERO_ID = -4;
    public static final int EXCHANGE_ID = -3;
    public static final int SECTOR_ID = -2;
    public static final int FRIEND_ID = -1;
    public static final int DAYS_30 = 40;
    public static final int DAYS_90 = 41;
    public static final int MOST_SKILLED_ID = 49;
    public static final int MONTHS_6 = 285;


    public static final int DAYS_ROI = 5555;//ROI
    public static final int POPULAR = 6666;//人气榜
    public static final int WEALTH = 8888;//土豪榜
    public static final int COMPETITION = 9999;//比赛榜
    public static final int COMPETITION_FOR_SCHOOL = 9998;//比赛榜

    //<editor-fold desc="Constructors">
    @Inject public LeaderboardDefKeyKnowledge()
    {
        super();
    }
    //</editor-fold>

    public static String getLeaderboardName(LeaderboardDefKey leaderboardDefKey)
    {
        if (leaderboardDefKey == null) return "";
        switch (leaderboardDefKey.key)
        {
            case POPULAR:
                return "人气榜";
            case WEALTH:
                return "土豪榜";
            case DAYS_30:
                return "月盈利榜";
            case DAYS_90:
                return "季度榜";
            case MONTHS_6:
                return "半年榜";
            case MOST_SKILLED_ID:
                return "总盈利榜";
        }
        return "";
    }

    @Nullable public Integer getLeaderboardDefIcon(@NotNull LeaderboardDefKey leaderboardDefKey)
    {
        return null;
    }
}
