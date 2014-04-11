package com.tradehero.th.api.discussion.form;

import com.tradehero.th.api.discussion.MessageType;

/**
 * Created by xavier2 on 2014/4/11.
 */
public class MessageCreateFormDTO
{
    public MessageType messageType;
    public String message;

    public MessageCreateFormDTO(MessageType messageType)
    {
        this.messageType = messageType;
    }

    public MessageCreateFormDTO(MessageType messageType, String message)
    {
        this.messageType = messageType;
        this.message = message;
    }
}
