package com.tradehero.common.billing;

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
                ExceptionType>,
        ExceptionType extends Exception>
{
    void forgetRequestCode(int requestCode);
    boolean isBillingAvailable();

    BillingPurchaseFinishedListenerType getPurchaseFinishedListener(int requestCode);
    int registerPurchaseFinishedListener(BillingPurchaseFinishedListenerType purchaseFinishedListener);
    void launchPurchaseSequence(int requestCode, PurchaseOrderType purchaseOrder);
}
