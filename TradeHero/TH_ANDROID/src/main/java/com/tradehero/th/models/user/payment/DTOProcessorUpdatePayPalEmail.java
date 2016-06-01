package com.ayondo.academy.models.user.payment;

import android.support.annotation.NonNull;
import com.ayondo.academy.api.users.UserBaseKey;
import com.ayondo.academy.api.users.UserProfileDTO;
import com.ayondo.academy.api.users.payment.UpdatePayPalEmailDTO;
import com.ayondo.academy.api.users.payment.UpdatePayPalEmailFormDTO;
import com.ayondo.academy.models.ThroughDTOProcessor;
import com.ayondo.academy.persistence.user.UserProfileCacheRx;

public class DTOProcessorUpdatePayPalEmail extends ThroughDTOProcessor<UpdatePayPalEmailDTO>
{
    @NonNull private final UserProfileCacheRx userProfileCache;
    @NonNull private final UserBaseKey userBaseKey;
    @NonNull private final UpdatePayPalEmailFormDTO updatePayPalEmailFormDTO;

    public DTOProcessorUpdatePayPalEmail(
            @NonNull UserProfileCacheRx userProfileCache,
            @NonNull UserBaseKey userBaseKey,
            @NonNull UpdatePayPalEmailFormDTO updatePayPalEmailFormDTO)
    {
        this.userProfileCache = userProfileCache;
        this.userBaseKey = userBaseKey;
        this.updatePayPalEmailFormDTO = updatePayPalEmailFormDTO;
    }

    @Override public UpdatePayPalEmailDTO process(UpdatePayPalEmailDTO value)
    {
        UserProfileDTO cachedProfile = userProfileCache.getCachedValue(userBaseKey);
        if (cachedProfile != null)
        {
            cachedProfile.paypalEmailAddress = updatePayPalEmailFormDTO.newPayPalEmailAddress;
            userProfileCache.onNext(userBaseKey, cachedProfile);
        }
        return value;
    }
}
