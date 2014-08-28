package com.tradehero.th.billing;

import com.tradehero.common.billing.BillingPurchaseFetcherHolder;
import com.tradehero.common.billing.ProductIdentifier;
import com.tradehero.common.billing.exception.BillingException;

public interface THPurchaseFetcherHolder<
        ProductIdentifierType extends ProductIdentifier,
        THOrderIdType extends THOrderId,
        THProductPurchaseType extends THProductPurchase<ProductIdentifierType, THOrderIdType>,
        BillingExceptionType extends BillingException>
        extends BillingPurchaseFetcherHolder<
        ProductIdentifierType,
        THOrderIdType,
        THProductPurchaseType,
        BillingExceptionType>
{
}
