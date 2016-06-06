package com.androidth.general.common.billing.samsung.rx;

import android.content.Context;
import android.support.annotation.NonNull;
import com.androidth.general.common.billing.samsung.SamsungBillingMode;
import java.lang.ref.WeakReference;

public class SamsungQueryPackage
{
    @NonNull public final WeakReference<Context> weakContext;
    @SamsungBillingMode public final int mode;

    //<editor-fold desc="Constructors">
    public SamsungQueryPackage(@NonNull Context context, int mode)
    {
        this.weakContext = new WeakReference<>(context);
        this.mode = mode;
    }
    //</editor-fold>
}
