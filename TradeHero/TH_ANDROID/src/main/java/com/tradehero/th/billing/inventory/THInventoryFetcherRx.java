package com.tradehero.th.billing.inventory;

import com.tradehero.common.billing.ProductIdentifier;
import com.tradehero.common.billing.inventory.BillingInventoryFetcherRx;
import com.tradehero.th.billing.THProductDetail;

public interface THInventoryFetcherRx<
        ProductIdentifierType extends ProductIdentifier,
        THProductDetailType extends THProductDetail<ProductIdentifierType>>
        extends BillingInventoryFetcherRx<
        ProductIdentifierType,
        THProductDetailType>
{
}
