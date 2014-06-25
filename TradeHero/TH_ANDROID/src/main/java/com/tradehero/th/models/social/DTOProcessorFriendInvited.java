package com.tradehero.th.models.social;

import com.tradehero.th.api.leaderboard.position.LeaderboardFriendsKey;
import com.tradehero.th.models.DTOProcessor;
import com.tradehero.th.persistence.leaderboard.position.LeaderboardFriendsCache;
import retrofit.client.Response;

public class DTOProcessorFriendInvited implements DTOProcessor<Response>
{
    private final LeaderboardFriendsCache leaderboardFriendsCache;

    public DTOProcessorFriendInvited(LeaderboardFriendsCache leaderboardFriendsCache)
    {
        this.leaderboardFriendsCache = leaderboardFriendsCache;
    }

    @Override public Response process(Response value)
    {
        leaderboardFriendsCache.invalidate(new LeaderboardFriendsKey());
        return value;
    }
}
