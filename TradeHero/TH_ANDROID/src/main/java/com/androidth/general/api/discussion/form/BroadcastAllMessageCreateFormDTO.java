package com.androidth.general.api.discussion.form;

import android.support.annotation.NonNull;
import com.androidth.general.api.discussion.MessageType;

public class BroadcastAllMessageCreateFormDTO extends MessageCreateFormDTO
{
    public static final MessageType TYPE = MessageType.BROADCAST_ALL_FOLLOWERS;

    //<editor-fold desc="Constructors">
    public BroadcastAllMessageCreateFormDTO()
    {
        super();
    }
    //</editor-fold>

    @NonNull @Override public MessageType getMessageType()
    {
        return TYPE;
    }
}
