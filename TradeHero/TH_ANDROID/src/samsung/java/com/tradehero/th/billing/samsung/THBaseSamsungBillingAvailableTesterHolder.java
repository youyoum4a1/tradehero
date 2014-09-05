package com.tradehero.th.billing.samsung;

import com.tradehero.common.billing.samsung.BaseSamsungBillingAvailableTesterHolder;
import com.tradehero.common.billing.samsung.exception.SamsungException;
import javax.inject.Inject;
import javax.inject.Provider;
import org.jetbrains.annotations.NotNull;

public class THBaseSamsungBillingAvailableTesterHolder
    extends BaseSamsungBillingAvailableTesterHolder<
        THSamsungBillingAvailableTester,
        SamsungException>
    implements THSamsungBillingAvailableTesterHolder
{
    //<editor-fold desc="Constructors">
    @Inject public THBaseSamsungBillingAvailableTesterHolder(
            @NotNull Provider<THSamsungBillingAvailableTester> thSamsungBillingAvailableTesterProvider)
    {
        super(thSamsungBillingAvailableTesterProvider);
    }
    //</editor-fold>
}
