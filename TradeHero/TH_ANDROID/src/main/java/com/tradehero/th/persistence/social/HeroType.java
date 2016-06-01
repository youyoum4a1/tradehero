package com.ayondo.academy.persistence.social;

import android.support.annotation.NonNull;
import com.fasterxml.jackson.annotation.JsonCreator;

public enum HeroType
{
    ALL(2, "All"),
    FREE(1, "Free"),
    PREMIUM(0, "Premium"),
    ;

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
    public static HeroType fromDescription(@NonNull String description)
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