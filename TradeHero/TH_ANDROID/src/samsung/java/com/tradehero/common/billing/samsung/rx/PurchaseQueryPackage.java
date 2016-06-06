package com.androidth.general.common.billing.samsung.rx;

import android.content.Context;
import android.support.annotation.NonNull;
import com.androidth.general.common.billing.samsung.SamsungBillingMode;

public class PurchaseQueryPackage extends SamsungQueryPackage
{
    @NonNull public final String itemId;
    public final boolean showSuccessDialog;

    //<editor-fold desc="Constructors">
    public PurchaseQueryPackage(
            @NonNull Context context,
            @SamsungBillingMode int mode,
            @NonNull String itemId,
            boolean showSuccessDialog)
    {
        super(context, mode);
        this.itemId = itemId;
        this.showSuccessDialog = showSuccessDialog;
    }
    //</editor-fold>

    @Override public int hashCode()
    {
        Context context = weakContext.get();
        return (context == null ? 0 : context.hashCode())
                ^ Integer.valueOf(mode).hashCode()
                ^ itemId.hashCode()
                ^ (showSuccessDialog ? 1 : 0);
    }

    @Override public boolean equals(Object o)
    {
        if (o == this)
        {
            return true;
        }
        if (!(o instanceof PurchaseQueryPackage))
        {
            return false;
        }

        Context context = weakContext.get();
        Context otherContext = ((PurchaseQueryPackage) o).weakContext.get();

        return (context == null ? otherContext == null : context.equals(otherContext))
                && (mode == ((PurchaseQueryPackage) o).mode)
                && (itemId.equals(((PurchaseQueryPackage) o).itemId))
                && (showSuccessDialog == ((PurchaseQueryPackage) o).showSuccessDialog);
    }
}
