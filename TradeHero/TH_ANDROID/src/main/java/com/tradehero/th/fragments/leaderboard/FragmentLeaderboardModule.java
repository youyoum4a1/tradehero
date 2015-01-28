package com.tradehero.th.fragments.leaderboard;

import com.tradehero.th.fragments.leaderboard.filter.LeaderboardFilterFragment;
import com.tradehero.th.fragments.leaderboard.filter.LeaderboardFilterSliderContainer;
import com.tradehero.th.fragments.leaderboard.main.LeaderboardCommunityFragment;
import dagger.Module;

@Module(
        injects = {
                LeaderboardFriendsItemView.class,
                LeaderboardDefListFragment.class,
                LeaderboardDefView.class,
                LeaderboardMarkUserListFragment.class,
                BaseLeaderboardFragment.class,
                LeaderboardMarkUserItemView.class,
                CompetitionLeaderboardMarkUserItemView.class,
                CompetitionLeaderboardMarkUserOwnRankingView.class,
                LeaderboardMarkUserOwnRankingView.class,
                FriendLeaderboardMarkUserListFragment.class,
                CompetitionLeaderboardMarkUserListFragment.class,
                CompetitionLeaderboardMarkUserListClosedFragment.class,
                CompetitionLeaderboardMarkUserListOnGoingFragment.class,
                CompetitionLeaderboardTimedHeader.class,
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
