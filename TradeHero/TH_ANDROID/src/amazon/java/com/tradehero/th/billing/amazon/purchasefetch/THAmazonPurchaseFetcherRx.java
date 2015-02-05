package com.tradehero.th.billing.amazon.purchasefetch;

import com.tradehero.common.billing.amazon.AmazonSKU;
import com.tradehero.common.billing.amazon.purchasefetch.AmazonPurchaseFetcherRx;
import com.tradehero.th.billing.amazon.THAmazonOrderId;
import com.tradehero.th.billing.amazon.THAmazonPurchase;
import com.tradehero.th.billing.purchasefetch.THPurchaseFetcherRx;

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
