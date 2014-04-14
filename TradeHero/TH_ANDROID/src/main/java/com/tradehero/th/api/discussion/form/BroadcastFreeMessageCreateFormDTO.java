package com.tradehero.th.api.discussion.form;

import com.tradehero.th.api.discussion.MessageType;

public class BroadcastFreeMessageCreateFormDTO extends MessageCreateFormDTO
{
    public static final MessageType TYPE = MessageType.BROADCAST_FREE_FOLLOWERS;

    public BroadcastFreeMessageCreateFormDTO()
    {
        super();
    }

    public BroadcastFreeMessageCreateFormDTO(String message)
    {
        super(message);
    }

    @Override public MessageType getMessageType()
    {
        return TYPE;
    }
}
