package com.ayondo.academy.billing.samsung.report;

import com.tradehero.common.billing.samsung.SamsungSKU;
import com.ayondo.academy.billing.report.THPurchaseReporterRx;
import com.ayondo.academy.billing.samsung.THSamsungOrderId;
import com.ayondo.academy.billing.samsung.THSamsungPurchase;

public interface THSamsungPurchaseReporterRx
        extends THPurchaseReporterRx<
        SamsungSKU,
        THSamsungOrderId,
        THSamsungPurchase>
{
}
