package com.androidth.general.billing.purchasefetch;

import com.androidth.general.common.billing.ProductIdentifier;
import com.androidth.general.common.billing.purchasefetch.BillingPurchaseFetcherRx;
import com.androidth.general.billing.THOrderId;
import com.androidth.general.billing.THProductPurchase;

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
