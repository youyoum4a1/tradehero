package com.ayondo.academy.models.leaderboard.key;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import com.ayondo.academy.R;
import com.ayondo.academy.api.leaderboard.def.LeaderboardDefDTO;

public class LeaderboardDefKeyKnowledge
{
    // For fake leaderboard definition, hardcoded on client side
    public static final int INVITE_FRIENDS_ID = -6;
    public static final int FOLLOWER_ID = -5;
    public static final int HERO_ID = -4;
    public static final int EXCHANGE_ID = -3;
    public static final int SECTOR_ID = -2;
    public static final int FRIEND_ID = -1;
    public static final int TOP_TRADERS = 2;
    public static final int CALENDAR_11 = 21;
    public static final int CALENDAR_12 = 22;
    public static final int CALENDAR_01 = 23;
    public static final int CALENDAR_02 = 24;
    public static final int CALENDAR_03 = 25;
    public static final int CALENDAR_04 = 26;
    public static final int CALENDAR_05 = 27;
    public static final int CALENDAR_06 = 28;
    public static final int CALENDAR_07 = 29;
    public static final int CALENDAR_08 = 30;
    public static final int CALENDAR_09 = 31;
    public static final int CALENDAR_10 = 32;
    public static final int CALENDAR_11_B = 33;
    public static final int CALENDAR_12_B = 34;
    public static final int QUARTER_4 = 35;
    public static final int QUARTER_1 = 36;
    public static final int QUARTER_2 = 37;
    public static final int QUARTER_3 = 38;
    public static final int QUARTER_4_B = 39;
    public static final int DAYS_30 = 40;
    public static final int DAYS_90 = 41;
    public static final int MONTHS_6 = 285;
    public static final int MOST_SKILLED_ID = 49;

    private static final int[] HAS_FOREX_LEADERBOARDS = new int[] {MOST_SKILLED_ID, DAYS_30, DAYS_90};

    public static boolean hasForex(int leaderboardId)
    {
        for (int i = 0; i < HAS_FOREX_LEADERBOARDS.length; i++)
        {
            if (leaderboardId == HAS_FOREX_LEADERBOARDS[i])
            {
                return true;
            }
        }
        return false;
    }

    public static String getDesiredName(@NonNull Resources resources, @NonNull LeaderboardDefDTO leaderboardDefDTO)
    {
        if (leaderboardDefDTO.id == MOST_SKILLED_ID)
        {
            return resources.getString(R.string.leaderboard_community_most_skilled);
        }
        return leaderboardDefDTO.name;
    }
}
