package com.tradehero.th.fragments.leaderboard;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.SubMenu;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class LeaderboardSortHelper
{
    @Inject
    public LeaderboardSortHelper()
    {
    }

    public void addSortMenu(SubMenu subMenu, int flag)
    {
        for (LeaderboardSortType sortType: LeaderboardSortType.values())
        {
            if ((flag & sortType.getFlag()) != 0)
            {
                subMenu
                        .add(Menu.NONE, sortType.getFlag(), Menu.NONE, sortType.getTitle())
                        .setIcon(sortType.getResourceIcon());
            }
        }
        subMenu.getItem().setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
    }
}