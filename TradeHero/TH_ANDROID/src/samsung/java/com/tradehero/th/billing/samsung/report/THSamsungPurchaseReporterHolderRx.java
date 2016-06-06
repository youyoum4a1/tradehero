package com.androidth.general.billing.samsung.report;

import com.androidth.general.common.billing.samsung.SamsungSKU;
import com.androidth.general.billing.report.THPurchaseReporterHolderRx;
import com.androidth.general.billing.samsung.THSamsungOrderId;
import com.androidth.general.billing.samsung.THSamsungProductDetail;
import com.androidth.general.billing.samsung.THSamsungPurchase;

public interface THSamsungPurchaseReporterHolderRx
        extends THPurchaseReporterHolderRx<
        SamsungSKU,
        THSamsungProductDetail,
        THSamsungOrderId,
        THSamsungPurchase>
{
}
