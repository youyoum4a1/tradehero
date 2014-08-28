package com.tradehero.th.billing.samsung;

import com.tradehero.common.billing.samsung.SamsungSKU;
import com.tradehero.common.billing.samsung.exception.SamsungException;
import com.tradehero.th.billing.THPurchaseReporter;

public interface THSamsungPurchaseReporter
    extends THPurchaseReporter<
        SamsungSKU,
        THSamsungOrderId,
        THSamsungPurchase,
        SamsungException>
{
}
