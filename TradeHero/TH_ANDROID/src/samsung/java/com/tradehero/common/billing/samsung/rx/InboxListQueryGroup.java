package com.androidth.general.common.billing.samsung.rx;

import android.support.annotation.NonNull;

public class InboxListQueryGroup extends BaseListQueryGroup
{
    @NonNull public final String startDate;
    @NonNull public final String endDate;

    public InboxListQueryGroup(
            @NonNull Integer startNum,
            @NonNull Integer endNum,
            @NonNull String startDate,
            @NonNull String endDate)
    {
        super(startNum, endNum);
        this.startDate = startDate;
        this.endDate = endDate;
    }

    @Override public int hashCode()
    {
        return super.hashCode() ^ startDate.hashCode() ^ endDate.hashCode();
    }

    @Override public boolean equals(Object other)
    {
        return other != null
                && other.getClass().equals(getClass())
                && equals((InboxListQueryGroup) other);
    }

    protected boolean equals(@NonNull InboxListQueryGroup other)
    {
        return super.equals(other)
                && other.startDate.equals(startDate)
                && other.endDate.equals(endDate);
    }
}
