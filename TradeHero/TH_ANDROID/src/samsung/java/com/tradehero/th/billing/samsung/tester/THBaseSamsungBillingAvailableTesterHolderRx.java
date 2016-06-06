package com.androidth.general.billing.samsung.tester;

import android.content.Context;
import android.support.annotation.NonNull;
import com.androidth.general.common.billing.samsung.SamsungBillingMode;
import com.androidth.general.common.billing.samsung.tester.BaseSamsungBillingAvailableTesterHolderRx;
import javax.inject.Inject;

public class THBaseSamsungBillingAvailableTesterHolderRx
    extends BaseSamsungBillingAvailableTesterHolderRx
    implements THSamsungBillingAvailableTesterHolderRx
{
    //<editor-fold desc="Constructors">
    @Inject public THBaseSamsungBillingAvailableTesterHolderRx(
            @NonNull Context context,
            @SamsungBillingMode int mode)
    {
        super(context, mode);
    }
    //</editor-fold>

    @NonNull @Override protected THSamsungBillingAvailableTesterRx createTester(int requestCode)
    {
        return new THBaseSamsungBillingAvailableTesterRx(requestCode, context, mode);
    }
}
