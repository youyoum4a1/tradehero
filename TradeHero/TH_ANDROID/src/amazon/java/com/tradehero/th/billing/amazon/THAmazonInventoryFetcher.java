package com.tradehero.th.billing.amazon;

import com.tradehero.common.billing.amazon.AmazonInventoryFetcher;
import com.tradehero.common.billing.amazon.AmazonSKU;
import com.tradehero.common.billing.amazon.exception.AmazonException;
import com.tradehero.th.billing.THInventoryFetcher;

public interface THAmazonInventoryFetcher
        extends
        AmazonInventoryFetcher<
                        AmazonSKU,
                        THAmazonProductDetail,
                        AmazonException>,
        THInventoryFetcher<
                AmazonSKU,
                THAmazonProductDetail,
                AmazonException>
{
}
