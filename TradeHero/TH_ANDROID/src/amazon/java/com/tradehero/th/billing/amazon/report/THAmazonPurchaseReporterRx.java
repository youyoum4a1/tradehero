package com.tradehero.th.billing.amazon.report;

import com.tradehero.common.billing.amazon.AmazonSKU;
import com.tradehero.th.billing.amazon.THAmazonOrderId;
import com.tradehero.th.billing.amazon.THAmazonPurchase;
import com.tradehero.th.billing.report.THPurchaseReporterRx;

public interface THAmazonPurchaseReporterRx
        extends THPurchaseReporterRx<
        AmazonSKU,
        THAmazonOrderId,
        THAmazonPurchase>
{
}
