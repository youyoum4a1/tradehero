package com.tradehero.th.api.discussion.form;

import com.tradehero.th.api.discussion.MessageType;

/**
 * Created by xavier2 on 2014/4/11.
 */
public class BroadcastAllMessageCreateFormDTO extends MessageCreateFormDTO
{
    public BroadcastAllMessageCreateFormDTO(String message)
    {
        super(MessageType.BROADCAST_ALL_FOLLOWERS, message);
    }
}
