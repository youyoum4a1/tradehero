package com.ayondo.academy.billing.inventory;

import com.tradehero.common.billing.ProductIdentifier;
import com.tradehero.common.billing.inventory.BillingInventoryFetcherRx;
import com.ayondo.academy.billing.THProductDetail;

public interface THInventoryFetcherRx<
        ProductIdentifierType extends ProductIdentifier,
        THProductDetailType extends THProductDetail<ProductIdentifierType>>
        extends BillingInventoryFetcherRx<
        ProductIdentifierType,
        THProductDetailType>
{
}
