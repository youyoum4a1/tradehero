package com.ayondo.academy.billing.amazon.purchasefetch;

import com.tradehero.common.billing.amazon.AmazonSKU;
import com.tradehero.common.billing.amazon.purchasefetch.AmazonPurchaseFetcherRx;
import com.ayondo.academy.billing.amazon.THAmazonOrderId;
import com.ayondo.academy.billing.amazon.THAmazonPurchase;
import com.ayondo.academy.billing.purchasefetch.THPurchaseFetcherRx;

public interface THAmazonPurchaseFetcherRx
        extends
        AmazonPurchaseFetcherRx<
                AmazonSKU,
                THAmazonOrderId,
                THAmazonPurchase>,
        THPurchaseFetcherRx<
                AmazonSKU,
                THAmazonOrderId,
                THAmazonPurchase>
{
}
