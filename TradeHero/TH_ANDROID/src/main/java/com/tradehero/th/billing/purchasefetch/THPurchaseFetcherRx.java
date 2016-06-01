package com.ayondo.academy.billing.purchasefetch;

import com.tradehero.common.billing.ProductIdentifier;
import com.tradehero.common.billing.purchasefetch.BillingPurchaseFetcherRx;
import com.ayondo.academy.billing.THOrderId;
import com.ayondo.academy.billing.THProductPurchase;

public interface THPurchaseFetcherRx<
        ProductIdentifierType extends ProductIdentifier,
        THOrderIdType extends THOrderId,
        THProductPurchaseType extends THProductPurchase<ProductIdentifierType, THOrderIdType>>
        extends BillingPurchaseFetcherRx<
        ProductIdentifierType,
        THOrderIdType,
        THProductPurchaseType>
{
}
