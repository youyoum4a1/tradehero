package com.tradehero.th.api.position;

import android.support.annotation.Nullable;
import com.fasterxml.jackson.annotation.JsonCreator;
import timber.log.Timber;

public enum PositionStatus
{
    CLOSED(0),
    SHORT(1),
    LONG(2),
    FORCE_CLOSED(3),
    ;

    public final int value;

    //<editor-fold desc="Constructors">
    PositionStatus(int value)
    {
        this.value = value;
    }
    //</editor-fold>

    @JsonCreator @Nullable
    public static PositionStatus create(int value)
    {
        for (PositionStatus positionStatus : values())
        {
            if (positionStatus.value == value)
            {
                return positionStatus;
            }
        }
        Timber.e(new IllegalArgumentException(), "Unknown PositionStatus value %d", value);
        return null;
    }
}
