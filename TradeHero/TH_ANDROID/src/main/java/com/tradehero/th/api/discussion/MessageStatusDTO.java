package com.tradehero.th.api.discussion;

import com.tradehero.common.persistence.DTO;

public class MessageStatusDTO implements DTO
{
    public Integer recipientUserId;
    public Integer privateFreeRemainingCount;

    public boolean isUnlimited()
    {
        return privateFreeRemainingCount != null && privateFreeRemainingCount == -1;
    }

    public boolean canSendPrivate()
    {
        return isUnlimited() || privateFreeRemainingCount != null && privateFreeRemainingCount > 0;
    }
}
