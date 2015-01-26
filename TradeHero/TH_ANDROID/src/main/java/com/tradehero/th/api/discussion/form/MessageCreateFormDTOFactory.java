package com.tradehero.th.api.discussion.form;

import android.support.annotation.NonNull;
import com.tradehero.th.api.discussion.MessageType;
import javax.inject.Inject;

public class MessageCreateFormDTOFactory
{
    //<editor-fold desc="Constructors">
    @Inject public MessageCreateFormDTOFactory()
    {
        super();
    }
    //</editor-fold>

    @NonNull public MessageCreateFormDTO createEmpty(@NonNull MessageType messageType)
    {
        switch (messageType)
        {
            case PRIVATE:
                return new PrivateMessageCreateFormDTO();
            case BROADCAST_ALL_FOLLOWERS:
                return new BroadcastAllMessageCreateFormDTO();
            case BROADCAST_FREE_FOLLOWERS:
                return new BroadcastFreeMessageCreateFormDTO();
            case BROADCAST_PAID_FOLLOWERS:
                return new BroadcastPaidMessageCreateFormDTO();
        }
        throw new IllegalStateException("Invalid type of MessageType" + messageType);
    }
}
