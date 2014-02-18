package com.tradehero.th.fragments.leaderboard;

import com.tradehero.th.R;

/** Created with IntelliJ IDEA. User: tho Date: 10/23/13 Time: 3:36 PM Copyright (c) TradeHero */
@Deprecated
public enum LeaderboardSortType
{

    Roi(R.string.leaderboard_user_roi, R.drawable.sort_roi, R.drawable.sort_roi_white, R.layout.lbmu_item_roi_mode),
    //HeroQuotient(R.string.hero_quotient, R.drawable.sort_hq, R.drawable.sort_hq_white, R.layout.lbmu_item_hq_mode),
    Followers(R.string.leaderboard_sort_followers, R.drawable.sort_followers, R.drawable.sort_followers_white, R.layout.lbmu_item_followers_mode),
    Comments(R.string.leaderboard_sort_comments, R.drawable.sort_comments, R.drawable.sort_comments_white, R.layout.lbmu_item_comments_mode),
    SharpeRatio(R.string.leaderboard_user_sharpe_ratio, R.drawable.sort_sharpe, R.drawable.sort_sharpe_white, R.layout.lbmu_item_sharpe_mode);

    public static final String TAG = "LEADERBOARD_SORT_TYPE";

    public static LeaderboardSortType DefaultSortType = Roi;

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
        //HeroQuotient.flag = SORT_HERO_QUOTIENT;
        Followers.flag = SORT_FOLLOWERS;
        Comments.flag = SORT_COMMENTS;
        SharpeRatio.flag = SORT_SHARPE_RATIO;
    }

    private int layoutResourceId;

    /**
     *
     * @param resourceString Name of sort type
     * @param resourceIcon icon to display in sorting selection menu
     * @param selectedResourceIcon icon to be on ActionBar when selected
     * @param layoutResourceId list item layout resource
     */
    LeaderboardSortType(int resourceString, int resourceIcon, int selectedResourceIcon, int layoutResourceId)
    {
        this.title = resourceString;
        this.resourceIcon = resourceIcon;
        this.selectedResourceIcon = selectedResourceIcon;
        this.layoutResourceId = layoutResourceId;
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

    public static LeaderboardSortType byServerFlag(int flag)
    {
        for (LeaderboardSortType sortType: LeaderboardSortType.values())
        {
            if (sortType.flag == (1 << (flag-1)))
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

    public int getLayoutResourceId()
    {
        return layoutResourceId;
    }
}
