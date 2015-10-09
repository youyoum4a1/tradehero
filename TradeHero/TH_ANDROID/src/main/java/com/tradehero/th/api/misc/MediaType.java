package com.tradehero.th.api.misc;

import android.support.annotation.NonNull;
import com.fasterxml.jackson.annotation.JsonCreator;

public enum MediaType
{
    SecurityImage(0)
    ;

    public final int value;

    //<editor-fold desc="Constructors">
    MediaType(int value)
    {
        this.value = value;
    }
    //</editor-fold>

    @JsonCreator @NonNull
    public static MediaType getFromValue(int value)
    {
        for (MediaType candidate : values())
        {
            if (candidate.value == value)
            {
                return candidate;
            }
        }
        throw new IllegalArgumentException("Unknown MediaType value " + value);
    }
}
