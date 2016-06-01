package com.ayondo.academy.billing.amazon.inventory;

import com.tradehero.common.billing.amazon.AmazonSKU;
import com.tradehero.common.billing.amazon.inventory.AmazonInventoryFetcherHolderRx;
import com.ayondo.academy.billing.amazon.THAmazonProductDetail;
import com.ayondo.academy.billing.inventory.THInventoryFetcherHolderRx;

public interface THAmazonInventoryFetcherHolderRx
        extends
        AmazonInventoryFetcherHolderRx<
                AmazonSKU,
                THAmazonProductDetail>,
        THInventoryFetcherHolderRx<
                AmazonSKU,
                THAmazonProductDetail>
{
}
