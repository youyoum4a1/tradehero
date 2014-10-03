package com.tradehero.th.api.share;

import com.fasterxml.jackson.annotation.JsonCreator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum TrackShareStatus
{
    OK("OK"),
    ERROR("ERROR")
    ;

    public final String serialised;

    private TrackShareStatus(@NotNull String serialised)
    {
        this.serialised = serialised;
    }

    @JsonCreator @NotNull
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
