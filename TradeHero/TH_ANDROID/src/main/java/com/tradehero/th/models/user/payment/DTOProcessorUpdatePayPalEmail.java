package com.tradehero.th.models.user.payment;

import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.payment.UpdatePayPalEmailDTO;
import com.tradehero.th.models.DTOProcessor;
import com.tradehero.th.persistence.user.UserProfileCache;

public class DTOProcessorUpdatePayPalEmail implements DTOProcessor<UpdatePayPalEmailDTO>
{
    private final UserProfileCache userProfileCache;
    private final UserBaseKey playerId;

    public DTOProcessorUpdatePayPalEmail(
            UserProfileCache userProfileCache, UserBaseKey playerId)
    {
        this.userProfileCache = userProfileCache;
        this.playerId = playerId;
    }

    @Override public UpdatePayPalEmailDTO process(UpdatePayPalEmailDTO value)
    {
        userProfileCache.invalidate(playerId);
        return value;
    }
}
