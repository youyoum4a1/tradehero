package com.tradehero.th.billing.samsung;

import com.tradehero.common.billing.samsung.BaseSamsungBillingAvailableTesterHolder;
import com.tradehero.common.billing.samsung.exception.SamsungException;
import javax.inject.Inject;
import javax.inject.Provider;
import android.support.annotation.NonNull;

public class THBaseSamsungBillingAvailableTesterHolder
    extends BaseSamsungBillingAvailableTesterHolder<
        THSamsungBillingAvailableTester,
        SamsungException>
    implements THSamsungBillingAvailableTesterHolder
{
    //<editor-fold desc="Constructors">
    @Inject public THBaseSamsungBillingAvailableTesterHolder(
            @NonNull Provider<THSamsungBillingAvailableTester> thSamsungBillingAvailableTesterProvider)
    {
        super(thSamsungBillingAvailableTesterProvider);
    }
    //</editor-fold>
}
