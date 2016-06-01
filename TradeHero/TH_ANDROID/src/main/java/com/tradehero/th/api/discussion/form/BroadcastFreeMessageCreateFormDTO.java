package com.ayondo.academy.api.discussion.form;

import android.support.annotation.NonNull;
import com.ayondo.academy.api.discussion.MessageType;

public class BroadcastFreeMessageCreateFormDTO extends MessageCreateFormDTO
{
    public static final MessageType TYPE = MessageType.BROADCAST_FREE_FOLLOWERS;

    //<editor-fold desc="Constructors">
    public BroadcastFreeMessageCreateFormDTO()
    {
        super();
    }
    //</editor-fold>

    @NonNull @Override public MessageType getMessageType()
    {
        return TYPE;
    }
}
