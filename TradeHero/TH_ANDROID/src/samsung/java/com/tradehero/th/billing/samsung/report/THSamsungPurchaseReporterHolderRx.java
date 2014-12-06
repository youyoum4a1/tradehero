package com.tradehero.th.billing.samsung.report;

import com.tradehero.common.billing.samsung.SamsungSKU;
import com.tradehero.th.billing.report.THPurchaseReporterHolderRx;
import com.tradehero.th.billing.samsung.THSamsungOrderId;
import com.tradehero.th.billing.samsung.THSamsungProductDetail;
import com.tradehero.th.billing.samsung.THSamsungPurchase;

public interface THSamsungPurchaseReporterHolderRx
        extends THPurchaseReporterHolderRx<
        SamsungSKU,
        THSamsungProductDetail,
        THSamsungOrderId,
        THSamsungPurchase>
{
}
