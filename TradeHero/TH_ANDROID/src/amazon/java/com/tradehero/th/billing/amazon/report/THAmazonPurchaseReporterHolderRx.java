package com.ayondo.academy.billing.amazon.report;

import com.tradehero.common.billing.amazon.AmazonSKU;
import com.ayondo.academy.billing.amazon.THAmazonOrderId;
import com.ayondo.academy.billing.amazon.THAmazonProductDetail;
import com.ayondo.academy.billing.amazon.THAmazonPurchase;
import com.ayondo.academy.billing.report.THPurchaseReporterHolderRx;

public interface THAmazonPurchaseReporterHolderRx
        extends THPurchaseReporterHolderRx<
        AmazonSKU,
        THAmazonProductDetail,
        THAmazonOrderId,
        THAmazonPurchase>
{
}
