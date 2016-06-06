package com.androidth.general.billing.googleplay.report;

import com.androidth.general.common.billing.googleplay.IABSKU;
import com.androidth.general.billing.googleplay.THIABOrderId;
import com.androidth.general.billing.googleplay.THIABPurchase;
import com.androidth.general.billing.report.THPurchaseReporterRx;

public interface THIABPurchaseReporterRx
        extends THPurchaseReporterRx<
        IABSKU,
        THIABOrderId,
        THIABPurchase>
{
}
