package com.tradehero.th.models.user;

import com.tradehero.th.api.BaseResponseDTO;
import com.tradehero.th.api.users.UpdateReferralCodeDTO;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.models.DTOProcessor;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import org.jetbrains.annotations.NotNull;

public class DTOProcessorUpdateReferralCode implements DTOProcessor<BaseResponseDTO>
{
    @NotNull private final UserProfileCacheRx userProfileCache;
    @NotNull private final UpdateReferralCodeDTO updateReferralCodeDTO;
    @NotNull private final UserBaseKey invitedUserId;

    //<editor-fold desc="Constructors">
    public DTOProcessorUpdateReferralCode(
            @NotNull UserProfileCacheRx userProfileCache,
            @NotNull UpdateReferralCodeDTO updateReferralCodeDTO,
            @NotNull UserBaseKey invitedUserId)
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
