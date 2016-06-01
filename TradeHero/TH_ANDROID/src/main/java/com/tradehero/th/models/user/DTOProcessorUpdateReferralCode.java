package com.ayondo.academy.models.user;

import android.support.annotation.NonNull;
import com.ayondo.academy.api.BaseResponseDTO;
import com.ayondo.academy.api.users.UpdateReferralCodeDTO;
import com.ayondo.academy.api.users.UserBaseKey;
import com.ayondo.academy.api.users.UserProfileDTO;
import com.ayondo.academy.models.ThroughDTOProcessor;
import com.ayondo.academy.persistence.user.UserProfileCacheRx;

public class DTOProcessorUpdateReferralCode extends ThroughDTOProcessor<BaseResponseDTO>
{
    @NonNull private final UserProfileCacheRx userProfileCache;
    @NonNull private final UpdateReferralCodeDTO updateReferralCodeDTO;
    @NonNull private final UserBaseKey invitedUserId;

    //<editor-fold desc="Constructors">
    public DTOProcessorUpdateReferralCode(
            @NonNull UserProfileCacheRx userProfileCache,
            @NonNull UpdateReferralCodeDTO updateReferralCodeDTO,
            @NonNull UserBaseKey invitedUserId)
    {
        this.userProfileCache = userProfileCache;
        this.updateReferralCodeDTO = updateReferralCodeDTO;
        this.invitedUserId = invitedUserId;
    }
    //</editor-fold>

    @Override public BaseResponseDTO process(BaseResponseDTO value)
    {
        UserProfileDTO cachedProfile = userProfileCache.getCachedValue(invitedUserId);
        if (cachedProfile != null)
        {
            cachedProfile.inviteCode = updateReferralCodeDTO.inviteCode;
        }
        userProfileCache.get(invitedUserId);
        return value;
    }
}
