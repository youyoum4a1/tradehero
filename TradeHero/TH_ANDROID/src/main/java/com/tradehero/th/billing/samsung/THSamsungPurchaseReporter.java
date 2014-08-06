package com.tradehero.th.billing.samsung;

import com.tradehero.common.billing.samsung.SamsungSKU;
import com.tradehero.common.billing.samsung.exception.SamsungException;
import com.tradehero.th.billing.THPurchaseReporter;

/**
 * Created by xavier on 3/27/14.
 */
public interface THSamsungPurchaseReporter
    extends THPurchaseReporter<
        SamsungSKU,
        THSamsungOrderId,
        THSamsungPurchase,
        SamsungException>
{
}
