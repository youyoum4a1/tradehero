package com.tradehero.th.api.discussion.form;

import com.tradehero.th.api.discussion.MessageType;
import javax.inject.Inject;

public class MessageCreateFormDTOFactory
{
    @Inject public MessageCreateFormDTOFactory()
    {
        super();
    }

    public MessageCreateFormDTO createEmpty(MessageType messageType)
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
