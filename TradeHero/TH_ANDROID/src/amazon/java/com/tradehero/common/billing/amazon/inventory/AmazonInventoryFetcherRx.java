package com.tradehero.common.billing.amazon.inventory;

import com.tradehero.common.billing.amazon.AmazonActor;
import com.tradehero.common.billing.amazon.AmazonProductDetail;
import com.tradehero.common.billing.amazon.AmazonSKU;
import com.tradehero.common.billing.inventory.BillingInventoryFetcherRx;

public interface AmazonInventoryFetcherRx<
        AmazonSKUType extends AmazonSKU,
        AmazonProductDetailsType extends AmazonProductDetail<AmazonSKUType>>
    extends BillingInventoryFetcherRx<
            AmazonSKUType,
            AmazonProductDetailsType>,
        AmazonActor
{
}
