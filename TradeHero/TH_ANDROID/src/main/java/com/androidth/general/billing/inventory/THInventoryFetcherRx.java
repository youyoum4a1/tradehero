package com.androidth.general.billing.inventory;

import com.androidth.general.common.billing.ProductIdentifier;
import com.androidth.general.common.billing.inventory.BillingInventoryFetcherRx;
import com.androidth.general.billing.THProductDetail;

public interface THInventoryFetcherRx<
        ProductIdentifierType extends ProductIdentifier,
        THProductDetailType extends THProductDetail<ProductIdentifierType>>
        extends BillingInventoryFetcherRx<
        ProductIdentifierType,
        THProductDetailType>
{
}
