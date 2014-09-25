package com.tradehero.th.models.user;

import com.tradehero.th.api.BaseResponseDTO;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.models.DTOProcessor;
import com.tradehero.th.persistence.user.UserProfileCache;
import org.jetbrains.annotations.NotNull;

public class DTOProcessorUserDeleted implements DTOProcessor<BaseResponseDTO>
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

    @Override public BaseResponseDTO process(BaseResponseDTO value)
    {
        userProfileCache.invalidate(playerId);
        return value;
    }
}
