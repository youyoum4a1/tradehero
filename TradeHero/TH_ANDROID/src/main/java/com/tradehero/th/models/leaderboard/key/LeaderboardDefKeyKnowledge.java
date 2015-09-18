package com.tradehero.th.models.leaderboard.key;

import com.tradehero.th.api.leaderboard.key.LeaderboardDefKey;
import javax.inject.Inject;

public class LeaderboardDefKeyKnowledge {
    // For fake leaderboard definition, hardcoded on client side
    public static final int FOLLOWER_ID = -5;
    public static final int HERO_ID = -4;
    public static final int EXCHANGE_ID = -3;
    public static final int SECTOR_ID = -2;
    public static final int FRIEND_ID = -1;


    public static final int SEARCH_RECOMMEND = 1;//综合搜索的默认推荐
    public static final int DAYS_ROI = 2;//ROI
    public static final int POPULAR = 3;//人气榜
    public static final int WINRATIO = 4;//高胜率榜
    public static final int HOTSTOCK = 5;//热股榜
    public static final int WEALTH = 6;//土豪榜
    public static final int COMPETITION = 7;//比赛榜
    public static final int COMPETITION_FOR_SCHOOL = 8;//比赛榜
    public static final int BUY_WHAT = 9;//买什么榜
    public static final int TOTAL_ROI = 10;//榜

    //<editor-fold desc="Constructors">
    @Inject
    public LeaderboardDefKeyKnowledge() {
        super();
    }
    //</editor-fold>

    public static String getLeaderboardName(LeaderboardDefKey leaderboardDefKey) {
        if (leaderboardDefKey == null) return "";
        switch (leaderboardDefKey.key) {
            case SEARCH_RECOMMEND:
                return "推荐榜";
            case POPULAR:
                return "人气榜";
            case WINRATIO:
                return "高胜率榜";
            case HOTSTOCK:
                return "热股榜";
            case WEALTH:
                return "土豪榜";
            case TOTAL_ROI:
                return "总收益率榜";
        }
        return "";
    }
}
