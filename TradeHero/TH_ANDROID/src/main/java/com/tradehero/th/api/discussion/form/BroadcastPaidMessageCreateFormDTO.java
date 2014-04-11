package com.tradehero.th.api.discussion.form;

import com.tradehero.th.api.discussion.MessageType;

public class BroadcastPaidMessageCreateFormDTO extends MessageCreateFormDTO
{
    public static final MessageType TYPE = MessageType.BROADCAST_PAID_FOLLOWERS;

    public BroadcastPaidMessageCreateFormDTO()
    {
        super();
    }

    public BroadcastPaidMessageCreateFormDTO(String message)
    {
        super(message);
    }

    @Override public MessageType getMessageType()
    {
        return TYPE;
    }
}
