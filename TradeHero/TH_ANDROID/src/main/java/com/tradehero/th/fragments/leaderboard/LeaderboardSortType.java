package com.tradehero.th.fragments.leaderboard;

import com.tradehero.th.R;

/** Created with IntelliJ IDEA. User: tho Date: 10/23/13 Time: 3:36 PM Copyright (c) TradeHero */
public enum LeaderboardSortType
{

    Roi(R.string.roi, R.drawable.sort_roi, R.drawable.sort_roi_white),
    HeroQuotient(R.string.hero_quotient, R.drawable.sort_hq, R.drawable.sort_hq_white),
    Followers(R.string.followers, R.drawable.sort_followers, R.drawable.sort_followers_white),
    Comments(R.string.comments, R.drawable.sort_comments, R.drawable.sort_comments_white),
    SharpeRatio(R.string.sharpe_ratio, R.drawable.sort_sharpe, R.drawable.sort_sharpe_white);


    public static final String BUNDLE_FLAG = "LEADERBOARD_SORT_FLAG";
    public static LeaderboardSortType DefaultSortType = HeroQuotient;

    private static final int SORT_ROI = 0x1;
    private static final int SORT_HERO_QUOTIENT = 0x2;
    private static final int SORT_FOLLOWERS = 0x4;
    private static final int SORT_COMMENTS = 0x8;
    private static final int SORT_SHARPE_RATIO = 0x10;

    private final int resourceIcon;
    private final int title;
    private final int selectedResourceIcon;
    private int flag;

    static
    {
        Roi.flag = SORT_ROI;
        HeroQuotient.flag = SORT_HERO_QUOTIENT;
        Followers.flag = SORT_FOLLOWERS;
        Comments.flag = SORT_COMMENTS;
        SharpeRatio.flag = SORT_SHARPE_RATIO;
    }

    LeaderboardSortType(int resourceString, int resourceIcon, int selectedResourceIcon)
    {
        this.title = resourceString;
        this.resourceIcon = resourceIcon;
        this.selectedResourceIcon = selectedResourceIcon;
    }

    public int getResourceIcon()
    {
        return resourceIcon;
    }

    public int getFlag()
    {
        return flag;
    }

    public int getServerFlag()
    {
        return 1 + (flag == 0 ? 0 : 32 - Integer.numberOfLeadingZeros(flag - 1));
    }

    public int getTitle()
    {
        return title;
    }

    public static LeaderboardSortType byFlag(int flag)
    {
        for (LeaderboardSortType sortType: LeaderboardSortType.values())
        {
            if (sortType.flag == flag)
            {
                return sortType;
            }
        }
        return null;
    }

    public int getSelectedResourceIcon()
    {
        return selectedResourceIcon;
    }
}
