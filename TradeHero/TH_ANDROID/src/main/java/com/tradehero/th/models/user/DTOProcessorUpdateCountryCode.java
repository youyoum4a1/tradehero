package com.tradehero.th.models.user;

import com.tradehero.th.api.users.UpdateCountryCodeDTO;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.models.DTOProcessor;
import com.tradehero.th.persistence.user.UserProfileCache;
import org.jetbrains.annotations.NotNull;

public class DTOProcessorUpdateCountryCode implements DTOProcessor<UpdateCountryCodeDTO>
{
    @NotNull private final UserProfileCache userProfileCache;
    @NotNull private final UserBaseKey playerId;

    public DTOProcessorUpdateCountryCode(
            @NotNull UserProfileCache userProfileCache,
            @NotNull UserBaseKey playerId)
    {
        this.userProfileCache = userProfileCache;
        this.playerId = playerId;
    }

    @Override public UpdateCountryCodeDTO process(UpdateCountryCodeDTO value)
    {
        userProfileCache.getOrFetchAsync(playerId, true);
        return value;
    }
}
