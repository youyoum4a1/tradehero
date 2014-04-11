package com.tradehero.th.api.discussion.form;

import com.tradehero.th.api.discussion.MessageType;

/**
 * Created by xavier2 on 2014/4/11.
 */
public class BroadcastPaidMessageCreateFormDTO extends MessageCreateFormDTO
{
    public BroadcastPaidMessageCreateFormDTO(String message)
    {
        super(MessageType.BROADCAST_PAID_FOLLOWERS, message);
    }
}
