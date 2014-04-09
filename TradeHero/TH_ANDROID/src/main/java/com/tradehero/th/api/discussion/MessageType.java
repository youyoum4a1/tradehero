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
            for (MessageType type:arr)
            {
                if (type.typeId == id)
                {
                    return type;
                }
            }
            return null;

        }
    }