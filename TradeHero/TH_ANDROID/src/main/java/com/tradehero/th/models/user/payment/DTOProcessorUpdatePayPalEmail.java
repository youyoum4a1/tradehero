package com.tradehero.th.models.user.payment;

import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.payment.UpdatePayPalEmailDTO;
import com.tradehero.th.models.DTOProcessor;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import android.support.annotation.NonNull;

public class DTOProcessorUpdatePayPalEmail implements DTOProcessor<UpdatePayPalEmailDTO>
{
    @NonNull private final UserProfileCacheRx userProfileCache;
    @NonNull private final UserBaseKey userBaseKey;

    public DTOProcessorUpdatePayPalEmail(
            @NonNull UserProfileCacheRx userProfileCache,
            @NonNull UserBaseKey userBaseKey)
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
