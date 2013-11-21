package com.tradehero.th.fragments.leaderboard;

import com.actionbarsherlock.view.MenuItem;
import com.tradehero.th.R;

/** Created with IntelliJ IDEA. User: tho Date: 11/21/13 Time: 6:26 PM Copyright (c) TradeHero */
public class FriendLeaderboardMarkUserListViewFragment extends LeaderboardMarkUserListViewFragment
{
    @Override protected int getMenuResource()
    {
        return R.menu.friend_leaderboard_menu;
    }

    @Override public boolean onOptionsItemSelected(MenuItem item)
    {
        if (leaderboardMarkUserLoader != null)
        {
            boolean oldIncludeFoF = leaderboardMarkUserLoader.isIncludeFoF();
            switch (item.getItemId())
            {
                case R.id.friend_leaderboard_menu_show_friends_of_friends:
                    if (!oldIncludeFoF)
                    {
                        leaderboardMarkUserLoader.setIncludeFoF(true);
                        leaderboardMarkUserLoader.reload();
                    }
                    return true;
                case R.id.friend_leaderboard_menu_show_friends_only:
                    if (oldIncludeFoF)
                    {
                        leaderboardMarkUserLoader.setIncludeFoF(false);
                        leaderboardMarkUserLoader.reload();
                    }
                    return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
