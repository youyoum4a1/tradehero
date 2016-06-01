package com.ayondo.academy.billing.inventory;

import com.tradehero.common.billing.ProductIdentifier;
import com.tradehero.common.billing.inventory.BillingInventoryFetcherHolderRx;
import com.ayondo.academy.billing.THProductDetail;

public interface THInventoryFetcherHolderRx<
        ProductIdentifierType extends ProductIdentifier,
        THProductDetailType extends THProductDetail<ProductIdentifierType>>
        extends BillingInventoryFetcherHolderRx<
        ProductIdentifierType,
        THProductDetailType>
{
}
