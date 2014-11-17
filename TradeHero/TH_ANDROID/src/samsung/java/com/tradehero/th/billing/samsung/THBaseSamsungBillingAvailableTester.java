package com.tradehero.th.billing.samsung;

import android.content.Context;
import android.support.annotation.NonNull;
import com.tradehero.common.billing.samsung.BaseSamsungBillingAvailableTester;
import com.tradehero.common.billing.samsung.exception.SamsungException;
import com.tradehero.th.billing.samsung.exception.THSamsungExceptionFactory;

public class THBaseSamsungBillingAvailableTester
        extends BaseSamsungBillingAvailableTester<SamsungException>
        implements THSamsungBillingAvailableTester
{
    @NonNull protected final THSamsungExceptionFactory samsungExceptionFactory;

    //<editor-fold desc="Constructors">
    public THBaseSamsungBillingAvailableTester(
            int requestCode,
            @NonNull Context context,
            int mode,
            @NonNull THSamsungExceptionFactory samsungExceptionFactory)
    {
        super(requestCode, context, mode);
        this.samsungExceptionFactory = samsungExceptionFactory;
    }
    //</editor-fold>

    @Override protected SamsungException createSamsungException(int result)
    {
        return samsungExceptionFactory.create(result);
    }
}
