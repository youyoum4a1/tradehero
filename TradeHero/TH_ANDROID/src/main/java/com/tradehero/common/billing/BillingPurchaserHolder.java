package com.tradehero.common.billing;

import com.tradehero.common.billing.exception.BillingException;

/** Created with IntelliJ IDEA. User: xavier Date: 11/8/13 Time: 11:06 AM To change this template use File | Settings | File Templates. */
public interface BillingPurchaserHolder<
        ProductIdentifierType extends ProductIdentifier,
        PurchaseOrderType extends PurchaseOrder<ProductIdentifierType>,
        OrderIdType extends OrderId,
        ProductPurchaseType extends ProductPurchase<ProductIdentifierType, OrderIdType>,
        BillingPurchaseFinishedListenerType extends BillingPurchaser.OnPurchaseFinishedListener<
                ProductIdentifierType,
                PurchaseOrderType,
                OrderIdType,
                ProductPurchaseType,
                BillingExceptionType>,
        BillingExceptionType extends BillingException>
{
    boolean isUnusedRequestCode(int requestCode);
    BillingPurchaseFinishedListenerType getPurchaseFinishedListener(int requestCode);
    void registerPurchaseFinishedListener(int requestCode, BillingPurchaseFinishedListenerType purchaseFinishedListener);
    void unregisterPurchaseFinishedListener(int requestCode);
    void launchPurchaseSequence(int requestCode, PurchaseOrderType purchaseOrder);
    void onDestroy();
}
