package com.tradehero.th.billing.samsung;

import com.tradehero.common.billing.samsung.SamsungSKU;
import com.tradehero.common.billing.samsung.exception.SamsungException;

/**
 * Created by xavier on 3/27/14.
 */
public interface THSamsungPurchaseReporterHolder
    extends SamsungPurchaseReporterHolder<
        SamsungSKU,
        THSamsungOrderId,
        THSamsungPurchase,
        SamsungException>
{
}
