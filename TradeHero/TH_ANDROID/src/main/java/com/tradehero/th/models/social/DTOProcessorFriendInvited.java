package com.tradehero.th.models.social;

import com.tradehero.th.api.BaseResponseDTO;
import com.tradehero.th.api.leaderboard.position.LeaderboardFriendsKey;
import com.tradehero.th.models.DTOProcessor;
import com.tradehero.th.persistence.leaderboard.position.LeaderboardFriendsCache;

public class DTOProcessorFriendInvited implements DTOProcessor<BaseResponseDTO>
{
    private final LeaderboardFriendsCache leaderboardFriendsCache;

    public DTOProcessorFriendInvited(LeaderboardFriendsCache leaderboardFriendsCache)
    {
        this.leaderboardFriendsCache = leaderboardFriendsCache;
    }

    @Override public BaseResponseDTO process(BaseResponseDTO value)
    {
        leaderboardFriendsCache.invalidate(new LeaderboardFriendsKey());
        return value;
    }
}
