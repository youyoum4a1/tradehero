package com.tradehero.th.billing.amazon;

import com.tradehero.common.billing.amazon.AmazonPurchaseFetcher;
import com.tradehero.common.billing.amazon.AmazonSKU;
import com.tradehero.common.billing.amazon.exception.AmazonException;
import com.tradehero.th.billing.THPurchaseFetcher;

public interface THAmazonPurchaseFetcher
        extends
        AmazonPurchaseFetcher<
                AmazonSKU,
                THAmazonOrderId,
                THAmazonPurchase,
                AmazonException>,
        THPurchaseFetcher<
                AmazonSKU,
                THAmazonOrderId,
                THAmazonPurchase,
                AmazonException>
{
}
