package com.tradehero.th.fragments.leaderboard;

import com.tradehero.th.fragments.leaderboard.filter.LeaderboardFilterFragment;
import com.tradehero.th.fragments.leaderboard.filter.LeaderboardFilterSliderContainer;
import com.tradehero.th.fragments.leaderboard.main.LeaderboardCommunityAdapter;
import com.tradehero.th.fragments.leaderboard.main.LeaderboardCommunityFragment;
import dagger.Component;

/**
 * Created by tho on 9/9/2014.
 */
@Component
public interface FragmentLeaderboardComponent
{
    void injectLeaderboardFriendsItemView(LeaderboardFriendsItemView target);
    void injectLeaderboardDefListFragment(LeaderboardDefListFragment target);
    void injectLeaderboardDefView(LeaderboardDefView target);
    void injectLeaderboardMarkUserLoader(LeaderboardMarkUserLoader target);
    void injectLeaderboardMarkUserListFragment(LeaderboardMarkUserListFragment target);
    void injectBaseLeaderboardFragment(BaseLeaderboardFragment target);
    void injectLeaderboardMarkUserItemView(LeaderboardMarkUserItemView target);
    void injectCompetitionLeaderboardMarkUserItemView(CompetitionLeaderboardMarkUserItemView target);
    void injectCompetitionLeaderboardMarkUserOwnRankingView(CompetitionLeaderboardMarkUserOwnRankingView target);
    void injectLeaderboardMarkUserListAdapter(LeaderboardMarkUserListAdapter target);
    void injectLeaderboardMarkUserOwnRankingView(LeaderboardMarkUserOwnRankingView target);
    void injectFriendLeaderboardMarkUserListFragment(FriendLeaderboardMarkUserListFragment target);
    void injectCompetitionLeaderboardMarkUserListFragment(CompetitionLeaderboardMarkUserListFragment target);
    void injectCompetitionLeaderboardMarkUserListClosedFragment(CompetitionLeaderboardMarkUserListClosedFragment target);
    void injectCompetitionLeaderboardMarkUserListOnGoingFragment(CompetitionLeaderboardMarkUserListOnGoingFragment target);
    void injectCompetitionLeaderboardTimedHeader(CompetitionLeaderboardTimedHeader target);
    void injectLeaderboardCommunityAdapter(LeaderboardCommunityAdapter target);
    void injectLeaderboardCommunityFragment(LeaderboardCommunityFragment target);
    void injectLeaderboardFilterFragment(LeaderboardFilterFragment target);
    void injectLeaderboardFilterSliderContainer(LeaderboardFilterSliderContainer target);
}
