package com.tradehero.th.billing;

import com.tradehero.common.billing.BillingInventoryFetcher;
import com.tradehero.common.billing.ProductIdentifier;
import com.tradehero.common.billing.exception.BillingException;

public interface THInventoryFetcher<
        ProductIdentifierType extends ProductIdentifier,
        THProductDetailType extends THProductDetail<ProductIdentifierType>,
        BillingExceptionType extends BillingException>
        extends BillingInventoryFetcher<
        ProductIdentifierType,
        THProductDetailType,
        BillingExceptionType>
{
}
