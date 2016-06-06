package com.androidth.general.api.discussion.form;

import android.support.annotation.NonNull;
import com.androidth.general.api.discussion.MessageType;

public class BroadcastPaidMessageCreateFormDTO extends MessageCreateFormDTO
{
    public static final MessageType TYPE = MessageType.BROADCAST_PAID_FOLLOWERS;

    //<editor-fold desc="Constructors">
    public BroadcastPaidMessageCreateFormDTO()
    {
        super();
    }
    //</editor-fold>

    @NonNull @Override public MessageType getMessageType()
    {
        return TYPE;
    }
}
