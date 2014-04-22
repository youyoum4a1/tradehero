package com.tradehero.th.api.discussion.form;

import com.tradehero.th.api.discussion.MessageType;

public class PrivateMessageCreateFormDTO extends MessageCreateFormDTO
{
    public static final MessageType TYPE = MessageType.PRIVATE;

    public int recipientUserId;

    public PrivateMessageCreateFormDTO()
    {
        super();
    }

    public PrivateMessageCreateFormDTO(String message)
    {
        super(message);
    }

    @Override public MessageType getMessageType()
    {
        return TYPE;
    }
}
