package com.tradehero.common.billing;

import com.tradehero.common.billing.exception.BillingException;

/**
 * Created by xavier on 2/24/14.
 */
public interface BillingPurchaseFetcherHolder<
        ProductIdentifierType extends ProductIdentifier,
        OrderIdType extends OrderId,
        ProductPurchaseType extends ProductPurchase<ProductIdentifierType, OrderIdType>,
        PurchaseFetchedListenerType extends BillingPurchaseFetcher.OnPurchaseFetchedListener<
                ProductIdentifierType,
                OrderIdType,
                ProductPurchaseType,
                BillingExceptionType>,
        BillingExceptionType extends BillingException>
{
    boolean isUnusedRequestCode(int requestCode);
    PurchaseFetchedListenerType getPurchaseFetchedListener(int requestCode);
    void registerPurchaseFetchedListener(int requestCode, PurchaseFetchedListenerType purchaseFetchedListener);
    void unregisterPurchaseFetchedListener(int requestCode);
    void launchFetchPurchaseSequence(int requestCode);
    void onDestroy();
}
