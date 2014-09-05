package com.tradehero.common.billing;

import com.tradehero.common.billing.exception.BillingException;

public interface BillingPurchaseFetcherHolder<
        ProductIdentifierType extends ProductIdentifier,
        OrderIdType extends OrderId,
        ProductPurchaseType extends ProductPurchase<ProductIdentifierType, OrderIdType>,
        BillingExceptionType extends BillingException>
    extends RequestCodeHolder
{
    BillingPurchaseFetcher.OnPurchaseFetchedListener<
            ProductIdentifierType,
            OrderIdType,
            ProductPurchaseType,
            BillingExceptionType> getPurchaseFetchedListener(int requestCode);
    void registerPurchaseFetchedListener(int requestCode, BillingPurchaseFetcher.OnPurchaseFetchedListener<
            ProductIdentifierType,
            OrderIdType,
            ProductPurchaseType,
            BillingExceptionType> purchaseFetchedListener);
    void launchFetchPurchaseSequence(int requestCode);
}
