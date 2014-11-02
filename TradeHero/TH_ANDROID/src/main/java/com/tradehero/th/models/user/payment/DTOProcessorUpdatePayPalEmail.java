package com.tradehero.th.models.user.payment;

import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.payment.UpdatePayPalEmailDTO;
import com.tradehero.th.models.DTOProcessor;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import org.jetbrains.annotations.NotNull;

public class DTOProcessorUpdatePayPalEmail implements DTOProcessor<UpdatePayPalEmailDTO>
{
    @NotNull private final UserProfileCacheRx userProfileCache;
    @NotNull private final UserBaseKey userBaseKey;

    public DTOProcessorUpdatePayPalEmail(
            @NotNull UserProfileCacheRx userProfileCache,
            @NotNull UserBaseKey userBaseKey)
    {
        this.userProfileCache = userProfileCache;
        this.userBaseKey = userBaseKey;
    }

    @Override public UpdatePayPalEmailDTO process(UpdatePayPalEmailDTO value)
    {
        userProfileCache.invalidate(userBaseKey);
        return value;
    }
}
