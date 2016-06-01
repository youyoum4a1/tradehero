package com.ayondo.academy.models.leaderboard.key;

import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.ayondo.academy.R;
import com.ayondo.academy.api.leaderboard.key.LeaderboardDefKey;

public class LeaderboardDefIconKnowledge extends LeaderboardDefKeyKnowledge
{
    @Nullable @DrawableRes public static Integer getLeaderboardDefIcon(@NonNull LeaderboardDefKey leaderboardDefKey)
    {
        switch (leaderboardDefKey.key)
        {
            case HERO_ID:
                return R.drawable.icn_lb_heroes;

            case FOLLOWER_ID:
                return R.drawable.icn_lb_followers;

            case INVITE_FRIENDS_ID:
            case FRIEND_ID:
                return R.drawable.icn_lb_friends;

            case SECTOR_ID:
                return R.drawable.icn_lb_sectors;

            case EXCHANGE_ID:
                return R.drawable.icn_lb_exchanges;

            case TOP_TRADERS:
                return R.drawable.icn_lb_toptraders;

            case CALENDAR_11:
            case CALENDAR_11_B:
                return R.drawable.icn_lb_cal_11;

            case CALENDAR_12:
            case CALENDAR_12_B:
                return R.drawable.icn_lb_cal_12;

            case CALENDAR_01:
                return R.drawable.icn_lb_cal_01;

            case CALENDAR_02:
                return R.drawable.icn_lb_cal_02;

            case CALENDAR_03:
                return R.drawable.icn_lb_cal_03;

            case CALENDAR_04:
                return R.drawable.icn_lb_cal_04;

            case CALENDAR_05:
                return R.drawable.icn_lb_cal_05;

            case CALENDAR_06:
                return R.drawable.icn_lb_cal_06;

            case CALENDAR_07:
                return R.drawable.icn_lb_cal_07;

            case CALENDAR_08:
                return R.drawable.icn_lb_cal_08;

            case CALENDAR_09:
                return R.drawable.icn_lb_cal_09;

            case CALENDAR_10:
                return R.drawable.icn_lb_cal_10;

            case QUARTER_4:
            case QUARTER_4_B:
                return R.drawable.icn_lb_quarter4;

            case QUARTER_1:
                return R.drawable.icn_lb_quarter1;

            case QUARTER_2:
                return R.drawable.icn_lb_quarter2;

            case QUARTER_3:
                return R.drawable.icn_lb_quarter3;

            case DAYS_30:
                return R.drawable.icn_lb_30d;

            case DAYS_90:
                return R.drawable.icn_lb_90d;

            case MOST_SKILLED_ID:
                return R.drawable.icn_lb_most_skilled;

            case MONTHS_6:
                return R.drawable.icn_lb_6m;

            default:
                return null;
        }
    }
}
