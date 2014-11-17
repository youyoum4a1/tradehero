package com.tradehero.th.billing.samsung;

import android.content.Context;
import android.support.annotation.NonNull;
import com.tradehero.common.billing.samsung.BaseSamsungBillingAvailableTesterHolder;
import com.tradehero.common.billing.samsung.exception.SamsungException;
import com.tradehero.th.billing.samsung.exception.THSamsungExceptionFactory;
import javax.inject.Inject;

public class THBaseSamsungBillingAvailableTesterHolder
    extends BaseSamsungBillingAvailableTesterHolder<
        THSamsungBillingAvailableTester,
        SamsungException>
    implements THSamsungBillingAvailableTesterHolder
{
    @NonNull protected final Context context;
    protected final int mode;
    @NonNull protected final THSamsungExceptionFactory samsungExceptionFactory;

    //<editor-fold desc="Constructors">
    @Inject public THBaseSamsungBillingAvailableTesterHolder(
            @NonNull Context context,
            @ForSamsungBillingMode int mode,
            @NonNull THSamsungExceptionFactory samsungExceptionFactory)
    {
        super();
        this.context = context;
        this.mode = mode;
        this.samsungExceptionFactory = samsungExceptionFactory;
    }
    //</editor-fold>

    @NonNull @Override protected THSamsungBillingAvailableTester createBillingTester(int requestCode)
    {
        return new THBaseSamsungBillingAvailableTester(requestCode, context, mode, samsungExceptionFactory);
    }
}
