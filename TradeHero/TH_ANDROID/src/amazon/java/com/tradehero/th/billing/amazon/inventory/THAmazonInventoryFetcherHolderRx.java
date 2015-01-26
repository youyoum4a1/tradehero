package com.tradehero.th.billing.amazon.inventory;

import com.tradehero.common.billing.amazon.AmazonSKU;
import com.tradehero.common.billing.amazon.inventory.AmazonInventoryFetcherHolderRx;
import com.tradehero.th.billing.amazon.THAmazonProductDetail;
import com.tradehero.th.billing.inventory.THInventoryFetcherHolderRx;

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
