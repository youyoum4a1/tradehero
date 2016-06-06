package com.androidth.general.api.position;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.Comparator;
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

    public static class StatusComparator implements Comparator<PositionStatus>
    {
        @Override public int compare(@NonNull PositionStatus lhs, @NonNull PositionStatus rhs)
        {
            if (lhs.equals(rhs)) return 0;
            if (lhs.equals(PositionStatus.LONG)) return -1;
            if (rhs.equals(PositionStatus.LONG)) return 1;
            if (lhs.equals(PositionStatus.SHORT)) return -1;
            if (rhs.equals(PositionStatus.SHORT)) return 1;
            // Only CLOSED and FORCE_CLOSED remain
            return 0;
        }
    }
}
