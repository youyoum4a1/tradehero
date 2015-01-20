package com.tradehero.th.models.user.payment;

import android.support.annotation.NonNull;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.api.users.payment.UpdateAlipayAccountDTO;
import com.tradehero.th.api.users.payment.UpdateAlipayAccountFormDTO;
import com.tradehero.th.models.ThroughDTOProcessor;
import com.tradehero.th.persistence.user.UserProfileCacheRx;

public class DTOProcessorUpdateAlipayAccount extends ThroughDTOProcessor<UpdateAlipayAccountDTO>
{
    @NonNull private final UserProfileCacheRx userProfileCache;
    @NonNull private final UserBaseKey playerId;
    @NonNull private final UpdateAlipayAccountFormDTO updateAlipayAccountFormDTO;

    public DTOProcessorUpdateAlipayAccount(
            @NonNull UserProfileCacheRx userProfileCache,
            @NonNull UserBaseKey playerId,
            @NonNull UpdateAlipayAccountFormDTO updateAlipayAccountFormDTO)
    {
        this.userProfileCache = userProfileCache;
        this.playerId = playerId;
        this.updateAlipayAccountFormDTO = updateAlipayAccountFormDTO;
    }

    @Override public UpdateAlipayAccountDTO process(UpdateAlipayAccountDTO value)
    {
        UserProfileDTO cachedProfile = userProfileCache.getCachedValue(playerId);
        if (cachedProfile != null)
        {
            cachedProfile.alipayAccount = updateAlipayAccountFormDTO.newAlipayAccount;
            cachedProfile.alipayIdentityNumber = updateAlipayAccountFormDTO.userIdentityNumber;
            cachedProfile.alipayRealName = updateAlipayAccountFormDTO.userRealName;
            userProfileCache.onNext(playerId, cachedProfile);
        }
        return value;
    }
}
