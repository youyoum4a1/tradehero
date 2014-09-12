package com.tradehero.th.billing.samsung;

import com.tradehero.common.billing.samsung.BaseSamsungPurchaserHolder;
import com.tradehero.common.billing.samsung.SamsungSKU;
import com.tradehero.common.billing.samsung.exception.SamsungException;
import javax.inject.Inject;
import javax.inject.Provider;
import org.jetbrains.annotations.NotNull;

public class THBaseSamsungPurchaserHolder
    extends BaseSamsungPurchaserHolder<
        SamsungSKU,
        THSamsungPurchaseOrder,
        THSamsungOrderId,
        THSamsungPurchase,
        THSamsungPurchaser,
        SamsungException>
    implements THSamsungPurchaserHolder
{
    @NotNull protected final Provider<Activity> activityProvider;

    //<editor-fold desc="Constructors">
    @Inject public THBaseSamsungPurchaserHolder(
            @NotNull Provider<THSamsungPurchaser> thSamsungPurchaserProvider,
            @NotNull Provider<Activity> activityProvider)
    {
        super(thSamsungPurchaserProvider);
        this.activityProvider = activityProvider;
    }
    //</editor-fold>
}
