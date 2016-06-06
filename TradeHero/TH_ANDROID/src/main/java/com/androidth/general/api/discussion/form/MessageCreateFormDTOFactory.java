package com.androidth.general.api.discussion.form;

import android.support.annotation.NonNull;
import com.androidth.general.api.discussion.MessageType;

public class MessageCreateFormDTOFactory
{
    @NonNull public static MessageCreateFormDTO createEmpty(@NonNull MessageType messageType)
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
