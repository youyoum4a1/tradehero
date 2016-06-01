package com.ayondo.academy.api.pagination;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class RangeDTO implements Comparable<RangeDTO>
{
    @Nullable public final Integer maxCount;
    @Nullable public final Integer maxId;
    @Nullable public final Integer minId;

    public static RangeDTO create(
            @Nullable Integer maxCount,
            @Nullable Integer maxId,
            @Nullable Integer minId)
    {
        return new RangeDTO(maxCount, maxId, minId);
    }

    public RangeDTO(
            @Nullable Integer maxCount,
            @Nullable Integer maxId,
            @Nullable Integer minId)
    {
        this.maxCount = maxCount;
        this.maxId = maxId;
        this.minId = minId;
    }

    @Override public int hashCode()
    {
        int result = maxCount != null ? maxCount : 0;
        result = 31 * result + (maxId != null ? maxId : 0);
        result = 31 * result + (minId != null ? minId : 0);
        return result;
    }

    @Override public boolean equals(@Nullable Object other)
    {
        if (!(other instanceof RangeDTO))
        {
            return false;
        }
        if (other == this)
        {
            return true;
        }
        RangeDTO otherRange = (RangeDTO) other;
        return (maxCount == null ? otherRange.maxCount == null : maxCount.equals(otherRange.maxCount))
                && (maxId == null ? otherRange.maxId == null : maxId.equals(otherRange.maxId))
                && (minId == null ? otherRange.minId == null : minId.equals(otherRange.minId));
    }

    @Override public int compareTo(@NonNull RangeDTO another)
    {
        if (maxId != null && another.maxId != null)
        {
            return maxId - another.maxId;
        }

        if (minId != null && another.minId != null)
        {
            return minId - another.minId;
        }

        return 0;
    }
}
