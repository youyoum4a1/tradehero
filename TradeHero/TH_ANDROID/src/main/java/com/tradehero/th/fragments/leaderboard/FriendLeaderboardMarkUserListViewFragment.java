package com.tradehero.th.fragments.leaderboard;

import android.os.Bundle;
import com.actionbarsherlock.view.MenuItem;
import com.tradehero.th.R;
import com.tradehero.th.api.leaderboard.LeaderboardDTO;

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
                        setFriendOfFriendFilter(true);
                    }
                    return true;
                case R.id.friend_leaderboard_menu_show_friends_only:
                    if (oldIncludeFoF)
                    {
                        setFriendOfFriendFilter(false);
                    }
                    return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void setFriendOfFriendFilter(boolean isFoF)
    {
        leaderboardMarkUserLoader.setIncludeFoF(isFoF);
        leaderboardMarkUserLoader.reload();
        invalidateCachedItemView();
    }
}
