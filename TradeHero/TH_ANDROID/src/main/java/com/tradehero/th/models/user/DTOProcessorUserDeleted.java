package com.tradehero.th.models.user;

import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.models.DTOProcessor;
import com.tradehero.th.persistence.user.UserProfileCache;
import retrofit.client.Response;

public class DTOProcessorUserDeleted implements DTOProcessor<Response>
{
    private final UserProfileCache userProfileCache;
    private final UserBaseKey playerId;

    public DTOProcessorUserDeleted(
            UserProfileCache userProfileCache, UserBaseKey playerId)
    {
        this.userProfileCache = userProfileCache;
        this.playerId = playerId;
    }

    @Override public Response process(Response value)
    {
        userProfileCache.invalidate(playerId);
        return value;
    }
}
