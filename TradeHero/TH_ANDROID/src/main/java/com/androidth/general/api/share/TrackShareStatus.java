package com.androidth.general.api.share;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.fasterxml.jackson.annotation.JsonCreator;

public enum TrackShareStatus
{
    OK("OK"),
    ERROR("ERROR")
    ;

    public final String serialised;

    TrackShareStatus(@NonNull String serialised)
    {
        this.serialised = serialised;
    }

    @JsonCreator @NonNull
    public static TrackShareStatus create(@Nullable String val)
    {
        TrackShareStatus[] values = TrackShareStatus.values();
        for (TrackShareStatus status : values)
        {
            if (status.serialised.equalsIgnoreCase(val))
            {
                return status;
            }
        }
        return ERROR;
    }
}
