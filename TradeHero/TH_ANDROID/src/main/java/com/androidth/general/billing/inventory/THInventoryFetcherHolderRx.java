package com.androidth.general.billing.inventory;

import com.androidth.general.common.billing.ProductIdentifier;
import com.androidth.general.common.billing.inventory.BillingInventoryFetcherHolderRx;
import com.androidth.general.billing.THProductDetail;

public interface THInventoryFetcherHolderRx<
        ProductIdentifierType extends ProductIdentifier,
        THProductDetailType extends THProductDetail<ProductIdentifierType>>
        extends BillingInventoryFetcherHolderRx<
        ProductIdentifierType,
        THProductDetailType>
{
}
