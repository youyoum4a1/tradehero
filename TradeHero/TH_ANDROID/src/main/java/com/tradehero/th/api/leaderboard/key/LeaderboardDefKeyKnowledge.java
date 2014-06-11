package com.tradehero.th.api.leaderboard.key;

import com.tradehero.th.R;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;

public class LeaderboardDefKeyKnowledge
{
    // For fake leaderboard definition, hardcoded on client side
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
    public static final int MOST_SKILLED_ID = 49;
    public static final int MONTHS_6 = 285;

    //<editor-fold desc="Constructors">
    @Inject public LeaderboardDefKeyKnowledge()
    {
        super();
    }
    //</editor-fold>

    public int getLeaderboardDefIcon(@NotNull LeaderboardDefKey leaderboardDefKey)
    {
        switch (leaderboardDefKey.key)
        {
            case HERO_ID:
                return R.drawable.icn_lb_heroes;

            case FOLLOWER_ID:
                return R.drawable.icn_lb_followers;

            case FRIEND_ID:
                return R.drawable.leaderboard_friends;

            case SECTOR_ID:
                return R.drawable.icn_lb_sectors;

            case EXCHANGE_ID:
                return R.drawable.icn_lb_exchanges;

            case TOP_TRADERS:
                return R.drawable.lb_toptraders;

            case CALENDAR_11:
            case CALENDAR_11_B:
                return R.drawable.lb_cal_11;

            case CALENDAR_12:
            case CALENDAR_12_B:
                return R.drawable.lb_cal_12;

            case CALENDAR_01:
                return R.drawable.lb_cal_01;

            case CALENDAR_02:
                return R.drawable.lb_cal_02;

            case CALENDAR_03:
                return R.drawable.lb_cal_03;

            case CALENDAR_04:
                return R.drawable.lb_cal_04;

            case CALENDAR_05:
                return R.drawable.lb_cal_05;

            case CALENDAR_06:
                return R.drawable.lb_cal_06;

            case CALENDAR_07:
                return R.drawable.lb_cal_07;

            case CALENDAR_08:
                return R.drawable.lb_cal_08;

            case CALENDAR_09:
                return R.drawable.lb_cal_09;

            case CALENDAR_10:
                return R.drawable.lb_cal_10;

            case QUARTER_4:
            case QUARTER_4_B:
                return R.drawable.lb_quarter4;

            case QUARTER_1:
                return R.drawable.lb_quarter1;

            case QUARTER_2:
                return R.drawable.lb_quarter2;

            case QUARTER_3:
                return R.drawable.lb_quarter3;

            case DAYS_30:
                return R.drawable.icn_lb_30d;

            case DAYS_90:
                return R.drawable.icn_lb_90d;

            case MOST_SKILLED_ID:
                return R.drawable.icn_lb_most_skilled;

            case MONTHS_6:
                return R.drawable.icn_lb_6m;

            default:
                return 0;
        }
    }
}
