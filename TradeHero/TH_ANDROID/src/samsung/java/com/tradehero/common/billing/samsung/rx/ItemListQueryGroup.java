package com.androidth.general.common.billing.samsung.rx;

import android.support.annotation.NonNull;

public class ItemListQueryGroup extends BaseListQueryGroup
{
    @NonNull public final String itemType;

    //<editor-fold desc="Constructors">
    public ItemListQueryGroup(
            int startNum,
            int endNum,
            @NonNull String itemType)
    {
        super(startNum, endNum);
        this.itemType = itemType;
    }
    //</editor-fold>

    @Override public int hashCode()
    {
        return super.hashCode() ^ itemType.hashCode();
    }

    @Override public boolean equals(Object other)
    {
        return other != null
                && other.getClass().equals(getClass())
                && equals((ItemListQueryGroup) other);
    }

    protected boolean equals(@NonNull ItemListQueryGroup other)
    {
        return super.equals(other)
                && other.itemType.equals(itemType);
    }
}
