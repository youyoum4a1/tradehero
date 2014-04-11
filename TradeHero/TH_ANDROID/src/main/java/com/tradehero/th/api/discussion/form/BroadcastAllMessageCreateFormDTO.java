package com.tradehero.th.api.discussion.form;

import com.tradehero.th.api.discussion.MessageType;

public class BroadcastAllMessageCreateFormDTO extends MessageCreateFormDTO
{
    public static final MessageType TYPE = MessageType.BROADCAST_ALL_FOLLOWERS;

    public BroadcastAllMessageCreateFormDTO()
    {
        super();
    }

    public BroadcastAllMessageCreateFormDTO(String message)
    {
        super(message);
    }

    @Override public MessageType getMessageType()
    {
        return TYPE;
    }
}
