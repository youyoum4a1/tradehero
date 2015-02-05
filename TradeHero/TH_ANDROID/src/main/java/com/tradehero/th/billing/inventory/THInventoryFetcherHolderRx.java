package com.tradehero.th.billing.inventory;

import com.tradehero.common.billing.ProductIdentifier;
import com.tradehero.common.billing.inventory.BillingInventoryFetcherHolderRx;
import com.tradehero.th.billing.THProductDetail;

public interface THInventoryFetcherHolderRx<
        ProductIdentifierType extends ProductIdentifier,
        THProductDetailType extends THProductDetail<ProductIdentifierType>>
        extends BillingInventoryFetcherHolderRx<
        ProductIdentifierType,
        THProductDetailType>
{
}
