package com.tradehero.common.billing.amazon.inventory;

import com.tradehero.common.billing.amazon.AmazonProductDetail;
import com.tradehero.common.billing.amazon.AmazonSKU;
import com.tradehero.common.billing.inventory.BillingInventoryFetcherHolderRx;

public interface AmazonInventoryFetcherHolderRx<
        AmazonSKUType extends AmazonSKU,
        AmazonProductDetailType extends AmazonProductDetail<AmazonSKUType>>
    extends BillingInventoryFetcherHolderRx<
            AmazonSKUType,
            AmazonProductDetailType>
{
}
