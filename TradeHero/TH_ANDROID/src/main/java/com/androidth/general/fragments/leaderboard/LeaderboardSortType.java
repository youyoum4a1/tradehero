package com.androidth.general.fragments.leaderboard;

import com.androidth.general.R;

@Deprecated
public enum LeaderboardSortType
{
    Roi(R.string.leaderboard_user_roi, R.drawable.sort_roi, R.drawable.sort_roi_white, R.layout.lbmu_item_roi_mode);

    public static final String SORT_TYPE_KEY = "LEADERBOARD_SORT_TYPE";

    public static LeaderboardSortType defaultSortType = Roi;

    private static final int SORT_ROI = 0x1;

    private final int resourceIcon;
    private final int title;
    private final int selectedResourceIcon;
    private int flag;

    static
    {
        Roi.flag = SORT_ROI;
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
