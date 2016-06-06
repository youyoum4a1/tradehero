package com.androidth.general.models.user.payment;

import android.support.annotation.NonNull;
import com.androidth.general.api.users.UserBaseKey;
import com.androidth.general.api.users.UserProfileDTO;
import com.androidth.general.api.users.payment.UpdatePayPalEmailDTO;
import com.androidth.general.api.users.payment.UpdatePayPalEmailFormDTO;
import com.androidth.general.models.ThroughDTOProcessor;
import com.androidth.general.persistence.user.UserProfileCacheRx;

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
