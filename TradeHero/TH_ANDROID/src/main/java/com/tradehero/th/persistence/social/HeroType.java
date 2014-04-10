package com.tradehero.th.persistence.social;

@Deprecated // TODO replace occurences with MessageType
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

    @Override public String toString()
    {
        switch (this)
        {
            case PREMIUM:
                return "Premium";
            case FREE:
                return "Free";
            case ALL:
                return "All";
        }
        return null;
    }

    public static HeroType fromId(int id)
    {
        HeroType[] arr = HeroType.values();
        for (HeroType type : arr)
        {
            if (type.typeId == id)
            {
                return type;
            }
        }
        return null;
    }

}