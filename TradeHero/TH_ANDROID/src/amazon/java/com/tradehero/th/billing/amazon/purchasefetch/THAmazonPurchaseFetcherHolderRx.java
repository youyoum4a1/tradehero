package com.ayondo.academy.billing.amazon.purchasefetch;

import com.tradehero.common.billing.amazon.AmazonSKU;
import com.tradehero.common.billing.amazon.purchasefetch.AmazonPurchaseFetcherHolderRx;
import com.ayondo.academy.billing.amazon.THAmazonOrderId;
import com.ayondo.academy.billing.amazon.THAmazonPurchase;
import com.ayondo.academy.billing.purchasefetch.THPurchaseFetcherHolderRx;

public interface THAmazonPurchaseFetcherHolderRx
        extends
        AmazonPurchaseFetcherHolderRx<
                AmazonSKU,
                THAmazonOrderId,
                THAmazonPurchase>,
        THPurchaseFetcherHolderRx<
                AmazonSKU,
                THAmazonOrderId,
                THAmazonPurchase>
{
}
