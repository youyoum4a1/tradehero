package com.tradehero.th.api.discussion.form;

import com.tradehero.th.api.discussion.MessageType;

abstract public class MessageCreateFormDTO
{
    public String message;

    public MessageCreateFormDTO()
    {
        super();
    }

    public MessageCreateFormDTO(String message)
    {
        this.message = message;
    }

    abstract public MessageType getMessageType();
}
