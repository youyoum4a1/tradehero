package com.tradehero.common.billing;

import com.tradehero.common.billing.exception.BillingException;

public interface BillingPurchaserHolder<
        ProductIdentifierType extends ProductIdentifier,
        PurchaseOrderType extends PurchaseOrder<ProductIdentifierType>,
        OrderIdType extends OrderId,
        ProductPurchaseType extends ProductPurchase<ProductIdentifierType, OrderIdType>,
        BillingExceptionType extends BillingException>
{
    boolean isUnusedRequestCode(int requestCode);
    void forgetRequestCode(int requestCode);
    BillingPurchaser.OnPurchaseFinishedListener<
            ProductIdentifierType,
            PurchaseOrderType,
            OrderIdType,
            ProductPurchaseType,
            BillingExceptionType> getPurchaseFinishedListener(int requestCode);
    void registerPurchaseFinishedListener(int requestCode, BillingPurchaser.OnPurchaseFinishedListener<
            ProductIdentifierType,
            PurchaseOrderType,
            OrderIdType,
            ProductPurchaseType,
            BillingExceptionType> purchaseFinishedListener);
    void launchPurchaseSequence(int requestCode, PurchaseOrderType purchaseOrder);
    void onDestroy();
}
