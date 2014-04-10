package com.tradehero.th.api.discussion;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum MessageType
{
    PRIVATE(1),
    BROADCAST_FREE_FOLLOWERS(2),
    BROADCAST_PAID_FOLLOWERS(3),
    BROADCAST_ALL_FOLLOWERS(4);

    public final int typeId;

    private MessageType(int typeId)
    {
        this.typeId = typeId;
    }

    @JsonCreator public static MessageType fromId(int id)
    {
        MessageType[] arr = MessageType.values();
        for (MessageType type : arr)
        {
            if (type.typeId == id)
            {
                return type;
            }
        }
        return null;
    }

    public static MessageType[] getShowingTypes()
    {
        MessageType[] r = new MessageType[3];
        r[0] = BROADCAST_PAID_FOLLOWERS;
        r[1] = BROADCAST_FREE_FOLLOWERS;
        r[2] = BROADCAST_ALL_FOLLOWERS;
        return r;
    }
        @Override public String toString()
        {
            switch (this)
            {
                case BROADCAST_PAID_FOLLOWERS:
                    return "Premium";
                case BROADCAST_FREE_FOLLOWERS:
                    return "Free";
                case BROADCAST_ALL_FOLLOWERS:
                    return "All";
            }
            return null;
        }

}