package com.androidth.general.common.billing.samsung.rx;

import android.content.Context;
import android.support.annotation.NonNull;
import com.androidth.general.common.billing.samsung.SamsungBillingMode;

public class InboxListQueryPackage extends SamsungQueryPackage
{
    @NonNull public final InboxListQueryGroup inboxListQueryGroup;

    //<editor-fold desc="Constructors">
    public InboxListQueryPackage(
            @NonNull Context context,
            @SamsungBillingMode int mode,
            @NonNull InboxListQueryGroup inboxListQueryGroup)
    {
        super(context, mode);
        this.inboxListQueryGroup = inboxListQueryGroup;
    }
    //</editor-fold>

    @Override public int hashCode()
    {
        Context context = weakContext.get();
        return (context == null ? 0 : context.hashCode())
                ^ Integer.valueOf(mode).hashCode()
                ^ inboxListQueryGroup.hashCode();
    }

    @Override public boolean equals(Object o)
    {
        if (o == this)
        {
            return true;
        }
        if (!(o instanceof InboxListQueryPackage))
        {
            return false;
        }

        Context context = weakContext.get();
        Context otherContext = ((InboxListQueryPackage) o).weakContext.get();

        return (context == null ? otherContext == null : context.equals(otherContext))
                && (mode == ((InboxListQueryPackage) o).mode)
                && (inboxListQueryGroup.equals(((InboxListQueryPackage) o).inboxListQueryGroup));
    }
}
