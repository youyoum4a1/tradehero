package com.tradehero.th.billing.amazon.report;

import com.tradehero.common.billing.amazon.AmazonSKU;
import com.tradehero.th.billing.amazon.THAmazonOrderId;
import com.tradehero.th.billing.amazon.THAmazonPurchase;
import com.tradehero.th.billing.report.THPurchaseReporterHolderRx;

public interface THAmazonPurchaseReporterHolderRx
        extends THPurchaseReporterHolderRx<
        AmazonSKU,
        THAmazonOrderId,
        THAmazonPurchase>
{
}
