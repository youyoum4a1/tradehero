package com.tradehero.th.models.user.payment;

import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.payment.UpdateAlipayAccountDTO;
import com.tradehero.th.models.DTOProcessor;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import android.support.annotation.NonNull;

public class DTOProcessorUpdateAlipayAccount implements DTOProcessor<UpdateAlipayAccountDTO>
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
