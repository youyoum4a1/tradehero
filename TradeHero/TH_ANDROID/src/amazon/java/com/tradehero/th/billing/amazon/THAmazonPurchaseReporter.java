package com.tradehero.th.billing.amazon;

import com.tradehero.common.billing.amazon.AmazonSKU;
import com.tradehero.common.billing.amazon.exception.AmazonException;
import com.tradehero.th.billing.THPurchaseReporter;

public interface THAmazonPurchaseReporter
    extends THPurchaseReporter<
        AmazonSKU,
        THAmazonOrderId,
        THAmazonPurchase,
        AmazonException>
{
}
