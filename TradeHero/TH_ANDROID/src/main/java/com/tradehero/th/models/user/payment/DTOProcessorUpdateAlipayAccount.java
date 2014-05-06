package com.tradehero.th.models.user.payment;

import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.payment.UpdateAlipayAccountDTO;
import com.tradehero.th.models.DTOProcessor;
import com.tradehero.th.persistence.user.UserProfileCache;

public class DTOProcessorUpdateAlipayAccount implements DTOProcessor<UpdateAlipayAccountDTO>
{
    private final UserProfileCache userProfileCache;
    private final UserBaseKey playerId;

    public DTOProcessorUpdateAlipayAccount(
            UserProfileCache userProfileCache, UserBaseKey playerId)
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
