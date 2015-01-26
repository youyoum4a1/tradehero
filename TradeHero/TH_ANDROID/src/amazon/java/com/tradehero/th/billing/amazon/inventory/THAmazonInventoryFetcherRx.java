package com.tradehero.th.billing.amazon.inventory;

import com.tradehero.common.billing.amazon.AmazonSKU;
import com.tradehero.common.billing.amazon.inventory.AmazonInventoryFetcherRx;
import com.tradehero.th.billing.amazon.THAmazonProductDetail;
import com.tradehero.th.billing.inventory.THInventoryFetcherRx;

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
