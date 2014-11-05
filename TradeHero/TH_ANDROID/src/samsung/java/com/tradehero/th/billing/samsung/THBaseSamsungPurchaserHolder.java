package com.tradehero.th.billing.samsung;

import com.tradehero.common.billing.samsung.BaseSamsungPurchaserHolder;
import com.tradehero.common.billing.samsung.SamsungSKU;
import com.tradehero.common.billing.samsung.exception.SamsungException;
import javax.inject.Inject;
import javax.inject.Provider;
import android.support.annotation.NonNull;

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
    //<editor-fold desc="Constructors">
    @Inject public THBaseSamsungPurchaserHolder(
            @NonNull Provider<THSamsungPurchaser> thSamsungPurchaserProvider)
    {
        super(thSamsungPurchaserProvider);
    }
    //</editor-fold>
}
