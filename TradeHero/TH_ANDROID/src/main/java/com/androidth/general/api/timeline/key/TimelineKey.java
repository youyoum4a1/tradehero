package com.androidth.general.api.timeline.key;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.androidth.general.common.persistence.DTOKey;
import com.androidth.general.api.pagination.RangeDTO;
import com.androidth.general.api.timeline.TimelineSection;
import com.androidth.general.api.users.UserBaseKey;

public class TimelineKey implements DTOKey
{
    @NonNull public final TimelineSection section;
    @NonNull public final UserBaseKey userBaseKey;
    @NonNull public final RangeDTO range;

    //<editor-fold desc="Constructors">
    public TimelineKey(
            @NonNull TimelineSection section,
            @NonNull UserBaseKey userBaseKey,
            @NonNull RangeDTO range)
    {
        this.section = section;
        this.userBaseKey = userBaseKey;
        this.range = range;
    }
    //</editor-fold>

    @Override public int hashCode()
    {
        return section.hashCode()
                ^ userBaseKey.hashCode()
                ^ range.hashCode();
    }

    @Override public boolean equals(@Nullable Object other)
    {
        if (!(other instanceof TimelineKey))
        {
            return false;
        }
        if (other == this)
        {
            return true;
        }
        TimelineKey key = (TimelineKey) other;
        return section.equals(key.section)
                && userBaseKey.equals(key.userBaseKey)
                && range.equals(key.range);
    }
}
