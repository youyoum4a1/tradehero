package com.tradehero.th.fragments.leaderboard;

import com.tradehero.th.fragments.competition.zone.CompetitionZonePrizePoolView;
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
                LeaderboardMarkUserLoader.class,
                LeaderboardMarkUserListFragment.class,
                BaseLeaderboardFragment.class,
                LeaderboardMarkUserItemView.class,
                CompetitionLeaderboardMarkUserItemView.class,
                CompetitionLeaderboardMarkUserOwnRankingView.class,
                LeaderboardMarkUserListAdapter.class,
                LeaderboardMarkUserOwnRankingView.class,
                FriendLeaderboardMarkUserListFragment.class,
                CompetitionLeaderboardMarkUserListFragment.class,
                CompetitionLeaderboardMarkUserListClosedFragment.class,
                CompetitionLeaderboardMarkUserListOnGoingFragment.class,
                CompetitionLeaderboardTimedHeader.class,
                LeaderboardCommunityAdapter.class,
                LeaderboardCommunityFragment.class,
                LeaderboardFilterFragment.class,
                LeaderboardFilterSliderContainer.class,
                CompetitionZonePrizePoolView.class
        },
        library = true,
        complete = false
)
public class FragmentLeaderboardModule
{
}
