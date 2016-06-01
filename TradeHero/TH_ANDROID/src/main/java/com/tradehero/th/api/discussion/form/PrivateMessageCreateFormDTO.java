package com.ayondo.academy.api.discussion.form;

import android.support.annotation.NonNull;
import com.ayondo.academy.api.discussion.MessageType;

public class PrivateMessageCreateFormDTO extends MessageCreateFormDTO
{
    public static final MessageType TYPE = MessageType.PRIVATE;

    public int recipientUserId;

    //<editor-fold desc="Constructors">
    public PrivateMessageCreateFormDTO()
    {
        super();
    }
    //</editor-fold>

    @NonNull @Override public MessageType getMessageType()
    {
        return TYPE;
    }
}
