package com.androidth.general.billing.samsung.report;

import com.androidth.general.common.billing.samsung.SamsungSKU;
import com.androidth.general.billing.report.THPurchaseReporterRx;
import com.androidth.general.billing.samsung.THSamsungOrderId;
import com.androidth.general.billing.samsung.THSamsungPurchase;

public interface THSamsungPurchaseReporterRx
        extends THPurchaseReporterRx<
        SamsungSKU,
        THSamsungOrderId,
        THSamsungPurchase>
{
}
