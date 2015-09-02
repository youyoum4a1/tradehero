package com.tradehero.th.fragments.leaderboard;

import com.tradehero.th.fragments.leaderboard.filter.LeaderboardFilterFragment;
import com.tradehero.th.fragments.leaderboard.filter.LeaderboardFilterSliderContainer;
import com.tradehero.th.fragments.leaderboard.main.LeaderboardCommunityFragment;
import dagger.Module;

@Module(
        injects = {
                LeaderboardMarkUserRecyclerFragment.class,
                LeaderboardMarkUserItemView.class,
                CompetitionLeaderboardMarkUserItemView.class,
                CompetitionLeaderboardMarkUserOwnRankingView.class,
                LeaderboardMarkUserOwnRankingView.class,
                FriendLeaderboardMarkUserRecyclerFragment.class,
                CompetitionLeaderboardMarkUserRecyclerFragment.class,
                CompetitionLeaderboardMarkUserRecyclerAdapter.class,
                CompetitionLeaderboardWrapperRecyclerAdapter.class,
                LeaderboardCommunityFragment.class,
                LeaderboardFilterFragment.class,
                LeaderboardFilterSliderContainer.class,
                LeaderboardMarkUserRecyclerAdapter.class,
                FriendsLeaderboardRecyclerAdapter.class,
                LeaderboardMarkUserListFragmentUtil.class,
        },
        library = true,
        complete = false
)
public class FragmentLeaderboardModule
{
}
