package com.androidth.general.models.user;

import android.support.annotation.NonNull;
import com.androidth.general.api.BaseResponseDTO;
import com.androidth.general.api.users.UpdateReferralCodeDTO;
import com.androidth.general.api.users.UserBaseKey;
import com.androidth.general.api.users.UserProfileDTO;
import com.androidth.general.models.ThroughDTOProcessor;
import com.androidth.general.persistence.user.UserProfileCacheRx;

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
