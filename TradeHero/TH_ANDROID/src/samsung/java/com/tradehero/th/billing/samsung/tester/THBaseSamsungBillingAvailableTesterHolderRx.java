package com.tradehero.th.billing.samsung.tester;

import android.content.Context;
import android.support.annotation.NonNull;
import com.tradehero.common.billing.samsung.tester.BaseSamsungBillingAvailableTesterHolderRx;
import com.tradehero.th.billing.samsung.ForSamsungBillingMode;
import javax.inject.Inject;

public class THBaseSamsungBillingAvailableTesterHolderRx
    extends BaseSamsungBillingAvailableTesterHolderRx
    implements THSamsungBillingAvailableTesterHolderRx
{
    //<editor-fold desc="Constructors">
    @Inject public THBaseSamsungBillingAvailableTesterHolderRx(
            @NonNull Context context,
            @ForSamsungBillingMode int mode)
    {
        super(context, mode);
    }
    //</editor-fold>

    @NonNull @Override protected THSamsungBillingAvailableTesterRx createTester(int requestCode)
    {
        return new THBaseSamsungBillingAvailableTesterRx(requestCode, context, mode);
    }
}