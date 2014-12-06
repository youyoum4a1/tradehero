package com.tradehero.th.billing.samsung.report;

import com.tradehero.common.billing.samsung.SamsungSKU;
import com.tradehero.th.billing.report.THPurchaseReporterRx;
import com.tradehero.th.billing.samsung.THSamsungOrderId;
import com.tradehero.th.billing.samsung.THSamsungPurchase;

public interface THSamsungPurchaseReporterRx
        extends THPurchaseReporterRx<
        SamsungSKU,
        THSamsungOrderId,
        THSamsungPurchase>
{
}
