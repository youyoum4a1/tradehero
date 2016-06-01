package com.ayondo.academy.billing.googleplay.report;

import com.tradehero.common.billing.googleplay.IABSKU;
import com.ayondo.academy.billing.googleplay.THIABOrderId;
import com.ayondo.academy.billing.googleplay.THIABProductDetail;
import com.ayondo.academy.billing.googleplay.THIABPurchase;
import com.ayondo.academy.billing.report.THPurchaseReporterHolderRx;

public interface THIABPurchaseReporterHolderRx extends
        THPurchaseReporterHolderRx<
                IABSKU,
                THIABProductDetail,
                THIABOrderId,
                THIABPurchase>
{
}
