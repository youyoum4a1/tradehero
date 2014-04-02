package com.tradehero.th.persistence.social;

public enum HeroType
{
        PREMIUM(0),
        FREE(1),
        ALL(2);

        public final int typeId;

        private HeroType(int id)
        {
            this.typeId = id;
        }


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