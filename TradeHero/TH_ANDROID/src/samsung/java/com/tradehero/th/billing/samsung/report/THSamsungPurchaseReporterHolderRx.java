package com.ayondo.academy.billing.samsung.report;

import com.tradehero.common.billing.samsung.SamsungSKU;
import com.ayondo.academy.billing.report.THPurchaseReporterHolderRx;
import com.ayondo.academy.billing.samsung.THSamsungOrderId;
import com.ayondo.academy.billing.samsung.THSamsungProductDetail;
import com.ayondo.academy.billing.samsung.THSamsungPurchase;

public interface THSamsungPurchaseReporterHolderRx
        extends THPurchaseReporterHolderRx<
        SamsungSKU,
        THSamsungProductDetail,
        THSamsungOrderId,
        THSamsungPurchase>
{
}
