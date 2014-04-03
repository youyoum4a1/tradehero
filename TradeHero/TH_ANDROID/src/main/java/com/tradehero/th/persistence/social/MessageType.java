package com.tradehero.th.persistence.social;

public enum MessageType
    {
        MESSAGE_TYPE_BROADCAST(0),
        MESSAGE_TYPE_WHISPER(1);

        public final int typeId;
        private MessageType(int typeId)
        {
            this.typeId = typeId;
        }
        //

        public static HeroType fromId(int id)
        {
            HeroType[] arr = HeroType.values();
            for (HeroType type:arr)
            {
                if (type.typeId == id)
                {
                    return type;
                }
            }
            return null;

        }
    }