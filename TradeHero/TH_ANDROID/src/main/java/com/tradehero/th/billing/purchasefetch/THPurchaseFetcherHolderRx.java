package com.tradehero.th.billing.purchasefetch;

import com.tradehero.common.billing.ProductIdentifier;
import com.tradehero.common.billing.purchasefetch.BillingPurchaseFetcherHolderRx;
import com.tradehero.th.billing.THOrderId;
import com.tradehero.th.billing.THProductPurchase;

public interface THPurchaseFetcherHolderRx<
        ProductIdentifierType extends ProductIdentifier,
        THOrderIdType extends THOrderId,
        THProductPurchaseType extends THProductPurchase<ProductIdentifierType, THOrderIdType>>
        extends BillingPurchaseFetcherHolderRx<
        ProductIdentifierType,
        THOrderIdType,
        THProductPurchaseType>
{
}
