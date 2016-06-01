package com.ayondo.academy.billing.samsung.tester;

import android.content.Context;
import android.support.annotation.NonNull;
import com.tradehero.common.billing.samsung.SamsungBillingMode;
import com.tradehero.common.billing.samsung.tester.BaseSamsungBillingAvailableTesterRx;

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
