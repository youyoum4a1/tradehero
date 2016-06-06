package com.androidth.general.billing.purchasefetch;

import com.androidth.general.common.billing.ProductIdentifier;
import com.androidth.general.common.billing.purchasefetch.BillingPurchaseFetcherHolderRx;
import com.androidth.general.billing.THOrderId;
import com.androidth.general.billing.THProductPurchase;

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
