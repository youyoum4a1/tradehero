package com.tradehero.th.billing.googleplay.report;

import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.th.billing.googleplay.THIABOrderId;
import com.tradehero.th.billing.googleplay.THIABPurchase;
import com.tradehero.th.billing.report.THPurchaseReporterRx;

public interface THIABPurchaseReporterRx
        extends THPurchaseReporterRx<
        IABSKU,
        THIABOrderId,
        THIABPurchase>
{
}
