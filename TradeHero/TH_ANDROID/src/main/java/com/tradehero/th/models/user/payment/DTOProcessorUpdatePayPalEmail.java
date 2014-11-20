package com.tradehero.th.models.user.payment;

import android.support.annotation.NonNull;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.api.users.payment.UpdatePayPalEmailDTO;
import com.tradehero.th.api.users.payment.UpdatePayPalEmailFormDTO;
import com.tradehero.th.models.ThroughDTOProcessor;
import com.tradehero.th.persistence.user.UserProfileCacheRx;

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
        UserProfileDTO cachedProfile = userProfileCache.getValue(userBaseKey);
        if (cachedProfile != null)
        {
            cachedProfile.paypalEmailAddress = updatePayPalEmailFormDTO.newPayPalEmailAddress;
            userProfileCache.onNext(userBaseKey, cachedProfile);
        }
        return value;
    }
}
