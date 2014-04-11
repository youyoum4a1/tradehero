package com.tradehero.th.api.discussion.form;

import com.tradehero.th.api.discussion.MessageType;

/**
 * Created by xavier2 on 2014/4/11.
 */
public class BroadcastFreeMessageCreateFormDTO extends MessageCreateFormDTO
{
    public BroadcastFreeMessageCreateFormDTO(String message)
    {
        super(MessageType.BROADCAST_FREE_FOLLOWERS, message);
    }
}
