package com.tradehero.th.fragments.leaderboard;

import com.tradehero.th.fragments.leaderboard.filter.LeaderboardFilterFragment;
import com.tradehero.th.fragments.leaderboard.filter.LeaderboardFilterSliderContainer;
import com.tradehero.th.fragments.leaderboard.main.LeaderboardCommunityAdapter;
import com.tradehero.th.fragments.leaderboard.main.LeaderboardCommunityFragment;
import dagger.Module;

/**
 * Created by tho on 9/9/2014.
 */
@Module(
        injects = {
                LeaderboardFriendsItemView.class,
                LeaderboardDefListFragment.class,
                LeaderboardDefView.class,
                LeaderboardMarkUserListFragment.class,
                BaseLeaderboardFragment.class,
                BaseLeaderboardMarkUserItemView.class,
                CompetitionLeaderboardMarkUserStockItemView.class,
                CompetitionLeaderboardMarkUserStockOwnRankingView.class,
                LeaderboardMarkUserListAdapter.class,
                LeaderboardMarkUserLoader.class,
                LeaderboardMarkUserStockOwnRankingView.class,
                FriendLeaderboardMarkUserListFragment.class,
                CompetitionLeaderboardMarkUserListFragment.class,
                CompetitionLeaderboardMarkUserListClosedFragment.class,
                CompetitionLeaderboardMarkUserListOnGoingFragment.class,
                CompetitionLeaderboardTimedHeader.class,
                LeaderboardCommunityAdapter.class,
                LeaderboardCommunityFragment.class,
                LeaderboardFilterFragment.class,
                LeaderboardFilterSliderContainer.class,
                LeaderboardMarkUserPagerFragment.class,
        },
        library = true,
        complete = false
)
public class FragmentLeaderboardModule
{
}
