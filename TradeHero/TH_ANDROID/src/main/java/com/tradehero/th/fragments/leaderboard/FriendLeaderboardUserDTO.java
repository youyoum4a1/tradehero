package com.tradehero.th.fragments.leaderboard;

import com.tradehero.th.adapters.ExpandableItem;

abstract public class FriendLeaderboardUserDTO implements ExpandableItem
{
    private boolean expanded;

    //<editor-fold desc="Constructors">
    protected FriendLeaderboardUserDTO(boolean expanded)
    {
        this.expanded = expanded;
    }
    //</editor-fold>

    @Override public boolean isExpanded()
    {
        return expanded;
    }

    @Override public void setExpanded(boolean expanded)
    {
        this.expanded = expanded;
    }
}
