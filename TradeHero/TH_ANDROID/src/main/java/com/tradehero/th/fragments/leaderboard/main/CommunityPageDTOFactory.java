package com.ayondo.academy.fragments.leaderboard.main;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Pair;
import com.android.internal.util.Predicate;
import com.ayondo.academy.api.leaderboard.def.LeaderboardDefDTO;
import com.ayondo.academy.api.leaderboard.def.LeaderboardDefDTOList;
import com.ayondo.academy.models.leaderboard.key.LeaderboardDefKeyKnowledge;

/** TODO IMHO, CommunityPageDTOFactory hides the fact about where the real data comes from */
class CommunityPageDTOFactory
{
    @NonNull public static LeaderboardDefDTOList reOrder(@NonNull LeaderboardDefDTOList list, @Nullable String countryCode)
    {
        LeaderboardDefDTOList reOrdered = new LeaderboardDefDTOList();
        reOrdered.addAll(list.findWhere(new Predicate<LeaderboardDefDTO>()
        {
            @Override public boolean apply(LeaderboardDefDTO leaderboardDefDTO)
            {
                return leaderboardDefDTO.isTimeRestrictedLeaderboard();
            }
        }));
        final LeaderboardDefDTO most = list.findFirstWhere(new Predicate<LeaderboardDefDTO>()
        {
            @Override public boolean apply(LeaderboardDefDTO leaderboardDefDTO)
            {
                return leaderboardDefDTO.id == LeaderboardDefKeyKnowledge.MOST_SKILLED_ID;
            }
        });
        if (most != null)
        {
            reOrdered.add(most);
        }
        final LeaderboardDefDTO friend = list.findFirstWhere(new Predicate<LeaderboardDefDTO>()
        {
            @Override public boolean apply(LeaderboardDefDTO leaderboardDefDTO)
            {
                return leaderboardDefDTO.id == LeaderboardDefKeyKnowledge.FRIEND_ID;
            }
        });
        if (friend != null)
        {
            reOrdered.add(friend);
        }
        final Pair<LeaderboardDefDTOList, LeaderboardDefDTOList> countryRegions = list.splitExchangeWithWithOut(
                countryCode != null ? countryCode : "fake");
        reOrdered.addAll(countryRegions.first);
        reOrdered.addAll(countryRegions.second);
        return reOrdered;
    }
}
