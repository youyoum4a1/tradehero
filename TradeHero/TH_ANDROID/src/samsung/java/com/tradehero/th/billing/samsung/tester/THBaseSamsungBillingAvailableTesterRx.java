package com.androidth.general.billing.samsung.tester;

import android.content.Context;
import android.support.annotation.NonNull;
import com.androidth.general.common.billing.samsung.SamsungBillingMode;
import com.androidth.general.common.billing.samsung.tester.BaseSamsungBillingAvailableTesterRx;

public class THBaseSamsungBillingAvailableTesterRx
        extends BaseSamsungBillingAvailableTesterRx
        implements THSamsungBillingAvailableTesterRx
{
    //<editor-fold desc="Constructors">
    public THBaseSamsungBillingAvailableTesterRx(
            int requestCode,
            @NonNull Context context,
            @SamsungBillingMode int mode)
    {
        super(requestCode, context, mode);
    }
    //</editor-fold>
}
