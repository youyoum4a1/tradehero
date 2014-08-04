package com.tradehero.th.models.user;

import com.tradehero.th.api.users.UpdateReferralCodeDTO;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.models.DTOProcessor;
import com.tradehero.th.persistence.user.UserProfileCache;
import org.jetbrains.annotations.NotNull;
import retrofit.client.Response;

public class DTOProcessorUpdateReferralCode implements DTOProcessor<Response>
{
    @NotNull private final UserProfileCache userProfileCache;
    @NotNull private final UpdateReferralCodeDTO updateReferralCodeDTO;
    @NotNull private final UserBaseKey invitedUserId;

    //<editor-fold desc="Constructors">
    public DTOProcessorUpdateReferralCode(
            @NotNull UserProfileCache userProfileCache,
            @NotNull UpdateReferralCodeDTO updateReferralCodeDTO,
            @NotNull UserBaseKey invitedUserId)
    {
        this.userProfileCache = userProfileCache;
        this.updateReferralCodeDTO = updateReferralCodeDTO;
        this.invitedUserId = invitedUserId;
    }
    //</editor-fold>

    @Override public Response process(Response value)
    {
        UserProfileDTO cachedProfile = userProfileCache.get(invitedUserId);
        if (cachedProfile != null)
        {
            cachedProfile.inviteCode = updateReferralCodeDTO.inviteCode;
        }
        userProfileCache.getOrFetchAsync(invitedUserId, true);
        return value;
    }
}
