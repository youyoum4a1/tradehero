package com.tradehero.common.billing;

import com.tradehero.common.billing.exception.BillingException;

/**
 * Created by xavier on 2/24/14.
 */
public interface BillingPurchaseRestorerHolder<
        ProductIdentifierType extends ProductIdentifier,
        OrderIdType extends OrderId,
        ProductPurchaseType extends ProductPurchase<ProductIdentifierType, OrderIdType>,
        BillingExceptionType extends BillingException>
{
    boolean isUnusedRequestCode(int requestCode);
    void forgetRequestCode(int requestCode);
    BillingPurchaseRestorer.OnPurchaseRestorerListener<
            ProductIdentifierType,
            OrderIdType,
            ProductPurchaseType,
            BillingExceptionType> getPurchaseRestorerListener(int requestCode);
    void registerPurchaseRestorerListener(int requestCode, BillingPurchaseRestorer.OnPurchaseRestorerListener<
            ProductIdentifierType,
            OrderIdType,
            ProductPurchaseType,
            BillingExceptionType> purchaseFetchedListener);
    void onDestroy();
}
