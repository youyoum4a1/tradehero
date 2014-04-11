package com.tradehero.th.api.discussion.form;

import com.tradehero.th.api.discussion.MessageType;

/**
 * Created by xavier2 on 2014/4/11.
 */
public class PrivateMessageCreateFormDTO extends MessageCreateFormDTO
{
    public PrivateMessageCreateFormDTO(String message)
    {
        super(MessageType.PRIVATE, message);
    }
}
