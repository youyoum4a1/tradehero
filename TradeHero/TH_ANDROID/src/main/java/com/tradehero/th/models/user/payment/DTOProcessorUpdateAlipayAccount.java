package com.tradehero.th.models.user.payment;

import android.support.annotation.NonNull;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.payment.UpdateAlipayAccountDTO;
import com.tradehero.th.models.ThroughDTOProcessor;
import com.tradehero.th.persistence.user.UserProfileCacheRx;

public class DTOProcessorUpdateAlipayAccount extends ThroughDTOProcessor<UpdateAlipayAccountDTO>
{
    @NonNull private final UserProfileCacheRx userProfileCache;
    @NonNull private final UserBaseKey playerId;

    public DTOProcessorUpdateAlipayAccount(
            @NonNull UserProfileCacheRx userProfileCache,
            @NonNull UserBaseKey playerId)
    {
        this.userProfileCache = userProfileCache;
        this.playerId = playerId;
    }

    @Override public UpdateAlipayAccountDTO process(UpdateAlipayAccountDTO value)
    {
        userProfileCache.invalidate(playerId);
        return value;
    }
}
