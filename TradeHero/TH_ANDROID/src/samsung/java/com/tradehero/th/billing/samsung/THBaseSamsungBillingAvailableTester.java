package com.tradehero.th.billing.samsung;

import android.content.Context;
import com.tradehero.common.billing.samsung.BaseSamsungBillingAvailableTester;
import com.tradehero.common.billing.samsung.exception.SamsungException;
import com.tradehero.th.billing.samsung.exception.THSamsungExceptionFactory;
import com.tradehero.th.utils.DaggerUtils;
import javax.inject.Inject;
import android.support.annotation.NonNull;

public class THBaseSamsungBillingAvailableTester
    extends BaseSamsungBillingAvailableTester<SamsungException>
    implements THSamsungBillingAvailableTester
{
    @NonNull protected final THSamsungExceptionFactory samsungExceptionFactory;

    //<editor-fold desc="Constructors">
    @Inject public THBaseSamsungBillingAvailableTester(
            @NonNull Context context,
            @ForSamsungBillingMode int mode,
            @NonNull THSamsungExceptionFactory samsungExceptionFactory)
    {
        super(context, mode);
        this.samsungExceptionFactory = samsungExceptionFactory;
    }
    //</editor-fold>

    @Override protected SamsungException createSamsungException(int result)
    {
        return samsungExceptionFactory.create(result);
    }
}
