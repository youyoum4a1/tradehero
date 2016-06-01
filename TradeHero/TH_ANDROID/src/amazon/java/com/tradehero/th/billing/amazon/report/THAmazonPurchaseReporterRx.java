package com.ayondo.academy.billing.amazon.report;

import com.tradehero.common.billing.amazon.AmazonSKU;
import com.ayondo.academy.billing.amazon.THAmazonOrderId;
import com.ayondo.academy.billing.amazon.THAmazonPurchase;
import com.ayondo.academy.billing.report.THPurchaseReporterRx;

public interface THAmazonPurchaseReporterRx
        extends THPurchaseReporterRx<
        AmazonSKU,
        THAmazonOrderId,
        THAmazonPurchase>
{
}
