package com.tradehero.th.fragments.leaderboard;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.actionbarsherlock.view.MenuItem;
import com.tradehero.th.R;
import com.tradehero.th.api.leaderboard.key.FriendsPerPagedLeaderboardKey;
import com.tradehero.th.api.leaderboard.key.PerPagedLeaderboardKey;
import com.tradehero.th.utils.metrics.localytics.LocalyticsConstants;

/** Created with IntelliJ IDEA. User: tho Date: 11/21/13 Time: 6:26 PM Copyright (c) TradeHero */
public class FriendLeaderboardMarkUserListFragment extends LeaderboardMarkUserListFragment
{
    @Override public void onResume()
    {
        super.onResume();

        localyticsSession.tagEvent(LocalyticsConstants.FriendsLeaderboard_Filter_FoF);
    }

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

    @Override protected PerPagedLeaderboardKey getInitialLeaderboardKey()
    {
        return new FriendsPerPagedLeaderboardKey(leaderboardId, null, null, false);
    }

    @Override protected void saveCurrentFilterKey()
    {
        // Do nothing really
    }

    @Override protected View inflateEmptyView(LayoutInflater inflater, ViewGroup container)
    {
        return inflater.inflate(R.layout.friend_leaderboard_empty_view, container, false);
    }

    private void setFriendOfFriendFilter(boolean isFoF)
    {
        currentLeaderboardKey = new FriendsPerPagedLeaderboardKey(
                currentLeaderboardKey.key,
                currentLeaderboardKey.page,
                currentLeaderboardKey.perPage,
                isFoF);
        leaderboardMarkUserLoader.setPagedLeaderboardKey(currentLeaderboardKey);
        leaderboardMarkUserLoader.reload();
        //invalidateCachedItemView();
    }
}
