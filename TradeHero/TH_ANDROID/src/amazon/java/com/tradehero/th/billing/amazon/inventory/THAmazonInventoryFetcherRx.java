package com.ayondo.academy.billing.amazon.inventory;

import com.tradehero.common.billing.amazon.AmazonSKU;
import com.tradehero.common.billing.amazon.inventory.AmazonInventoryFetcherRx;
import com.ayondo.academy.billing.amazon.THAmazonProductDetail;
import com.ayondo.academy.billing.inventory.THInventoryFetcherRx;

public interface THAmazonInventoryFetcherRx
        extends
        AmazonInventoryFetcherRx<
                AmazonSKU,
                THAmazonProductDetail>,
        THInventoryFetcherRx<
                AmazonSKU,
                THAmazonProductDetail>
{
}
