package com.androidth.general.common.billing.samsung.rx;

import android.content.Context;
import android.support.annotation.NonNull;
import com.androidth.general.common.billing.samsung.SamsungBillingMode;

public class ItemListQueryPackage extends SamsungQueryPackage
{
    @NonNull public final ItemListQueryGroup itemListQueryGroup;

    //<editor-fold desc="Constructors">
    public ItemListQueryPackage(
            @NonNull Context context,
            @SamsungBillingMode int mode,
            @NonNull ItemListQueryGroup itemListQueryGroup)
    {
        super(context, mode);
        this.itemListQueryGroup = itemListQueryGroup;
    }
    //</editor-fold>

    @Override public int hashCode()
    {
        Context context = weakContext.get();
        return (context == null ? 0 : context.hashCode())
                ^ Integer.valueOf(mode).hashCode()
                ^ itemListQueryGroup.hashCode();
    }

    @Override public boolean equals(Object o)
    {
        if (o == this)
        {
            return true;
        }
        if (!(o instanceof ItemListQueryPackage))
        {
            return false;
        }

        Context context = weakContext.get();
        Context otherContext = ((ItemListQueryPackage) o).weakContext.get();

        return (context == null ? otherContext == null : context.equals(otherContext))
                && (mode == ((ItemListQueryPackage) o).mode)
                && (itemListQueryGroup.equals(((ItemListQueryPackage) o).itemListQueryGroup));
    }
}
