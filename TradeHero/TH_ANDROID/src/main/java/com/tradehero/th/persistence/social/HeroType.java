package com.tradehero.th.persistence.social;

import com.fasterxml.jackson.annotation.JsonCreator;
import android.support.annotation.NonNull;

public enum HeroType
{
    PREMIUM(0, "Premium"),
    FREE(1, "Free"),
    ALL(2, "All");

    public final int typeId;
    @NonNull public final String description;

    private HeroType(int id, @NonNull String description)
    {
        this.typeId = id;
        this.description = description;
    }

    @Override public String toString()
    {
        return description;
    }

    @JsonCreator
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

    @JsonCreator
    public static HeroType fromDescription(String description)
    {
        HeroType[] arr = HeroType.values();
        for (HeroType type : arr)
        {
            if (type.description.equals(description))
            {
                return type;
            }
        }
        return null;
    }
}