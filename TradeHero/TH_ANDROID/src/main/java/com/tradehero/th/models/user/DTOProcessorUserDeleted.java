package com.tradehero.th.models.user;

import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.models.DTOProcessor;
import com.tradehero.th.persistence.user.UserProfileCache;
import org.jetbrains.annotations.NotNull;
import retrofit.client.Response;

public class DTOProcessorUserDeleted implements DTOProcessor<Response>
{
    @NotNull private final UserProfileCache userProfileCache;
    @NotNull private final UserBaseKey playerId;

    public DTOProcessorUserDeleted(
            @NotNull UserProfileCache userProfileCache,
            @NotNull UserBaseKey playerId)
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
