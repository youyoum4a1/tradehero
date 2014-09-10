package com.tradehero.th.billing.amazon;

import com.tradehero.common.billing.amazon.AmazonInventoryFetcherHolder;
import com.tradehero.common.billing.amazon.AmazonSKU;
import com.tradehero.common.billing.amazon.exception.AmazonException;
import com.tradehero.th.billing.THInventoryFetcherHolder;

public interface THAmazonInventoryFetcherHolder
        extends
        AmazonInventoryFetcherHolder<
                AmazonSKU,
                THAmazonProductDetail,
                AmazonException>,
        THInventoryFetcherHolder<
                AmazonSKU,
                THAmazonProductDetail,
                AmazonException>
{
}
