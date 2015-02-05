package com.tradehero.th.billing.amazon.purchasefetch;

import com.tradehero.common.billing.amazon.AmazonSKU;
import com.tradehero.common.billing.amazon.purchasefetch.AmazonPurchaseFetcherHolderRx;
import com.tradehero.th.billing.amazon.THAmazonOrderId;
import com.tradehero.th.billing.amazon.THAmazonPurchase;
import com.tradehero.th.billing.purchasefetch.THPurchaseFetcherHolderRx;

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
