package com.tradehero.th.billing.amazon;

import com.tradehero.common.billing.amazon.AmazonPurchaseFetcherHolder;
import com.tradehero.common.billing.amazon.AmazonSKU;
import com.tradehero.common.billing.amazon.exception.AmazonException;
import com.tradehero.th.billing.THPurchaseFetcherHolder;

public interface THAmazonPurchaseFetcherHolder
        extends
        AmazonPurchaseFetcherHolder<
                AmazonSKU,
                THAmazonOrderId,
                THAmazonPurchase,
                AmazonException>,
        THPurchaseFetcherHolder<
                AmazonSKU,
                THAmazonOrderId,
                THAmazonPurchase,
                AmazonException>
{
}
