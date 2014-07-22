package com.tradehero.th.models.user;

import com.tradehero.th.api.users.UpdateCountryCodeResultDTO;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.models.DTOProcessor;
import com.tradehero.th.persistence.user.UserProfileCache;
import org.jetbrains.annotations.NotNull;

public class DTOProcessorUpdateLocation implements DTOProcessor<UpdateCountryCodeResultDTO>
{
    @NotNull private final UserProfileCache userProfileCache;
    @NotNull private final UserBaseKey playerId;

    public DTOProcessorUpdateLocation(
            @NotNull UserProfileCache userProfileCache,
            @NotNull UserBaseKey playerId)
    {
        this.userProfileCache = userProfileCache;
        this.playerId = playerId;
    }

    @Override public UpdateCountryCodeResultDTO process(UpdateCountryCodeResultDTO value)
    {
        userProfileCache.invalidate(playerId);
        return value;
    }
}
