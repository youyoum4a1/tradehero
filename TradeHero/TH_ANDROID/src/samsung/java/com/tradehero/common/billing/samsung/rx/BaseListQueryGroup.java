package com.tradehero.common.billing.samsung.rx;

import android.support.annotation.NonNull;

public class BaseListQueryGroup
{
    @NonNull public final Integer startNum;
    @NonNull public final Integer endNum;
    @NonNull public final String groupId;

    //<editor-fold desc="Constructors">
    public BaseListQueryGroup(
            @NonNull Integer startNum,
            @NonNull Integer endNum,
            @NonNull String groupId)
    {
        this.startNum = startNum;
        this.endNum = endNum;
        this.groupId = groupId;
    }
    //</editor-fold>

    @Override public int hashCode()
    {
        return startNum.hashCode() ^ endNum.hashCode() ^ groupId.hashCode();
    }

    @Override public boolean equals(Object other)
    {
        return other != null
                && (other.getClass().equals(getClass()))
                && equals((BaseListQueryGroup) other);
    }

    protected boolean equals(@NonNull BaseListQueryGroup other)
    {
        return other.startNum.equals(startNum)
                && other.endNum.equals(endNum)
                && other.groupId.equals(groupId);
    }
}
