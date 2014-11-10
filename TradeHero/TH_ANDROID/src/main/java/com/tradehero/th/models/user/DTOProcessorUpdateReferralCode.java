package com.tradehero.th.models.user;

import android.support.annotation.NonNull;
import com.tradehero.th.api.BaseResponseDTO;
import com.tradehero.th.api.users.UpdateReferralCodeDTO;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.models.ThroughDTOProcessor;
import com.tradehero.th.persistence.user.UserProfileCacheRx;

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
        UserProfileDTO cachedProfile = userProfileCache.getValue(invitedUserId);
        if (cachedProfile != null)
        {
            cachedProfile.inviteCode = updateReferralCodeDTO.inviteCode;
        }
        userProfileCache.get(invitedUserId);
        return value;
    }
}
