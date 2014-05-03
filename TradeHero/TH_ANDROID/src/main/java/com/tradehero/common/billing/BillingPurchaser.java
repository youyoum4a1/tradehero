package com.tradehero.common.billing;

import com.tradehero.common.billing.exception.BillingException;


public interface BillingPurchaser<
        ProductIdentifierType extends ProductIdentifier,
        PurchaseOrderType extends PurchaseOrder<ProductIdentifierType>,
        OrderIdType extends OrderId,
        ProductPurchaseType extends ProductPurchase<ProductIdentifierType, OrderIdType>,
        BillingExceptionType extends BillingException>
{
    int getRequestCode();
    OnPurchaseFinishedListener<ProductIdentifierType, PurchaseOrderType, OrderIdType, ProductPurchaseType, BillingExceptionType> getPurchaseFinishedListener();
    void setPurchaseFinishedListener(OnPurchaseFinishedListener<ProductIdentifierType, PurchaseOrderType, OrderIdType, ProductPurchaseType, BillingExceptionType> purchaseFinishedListener);
    void purchase(int requestCode, PurchaseOrderType purchaseOrder);

    
    public static interface OnPurchaseFinishedListener<
            ProductIdentifierType extends ProductIdentifier,
            PurchaseOrderType extends PurchaseOrder<ProductIdentifierType>,
            OrderIdType extends OrderId,
            ProductPurchaseType extends ProductPurchase<ProductIdentifierType, OrderIdType>,
            BillingExceptionType extends BillingException>
    {
        /**
         * Called to notify that an in-app purchase finished. If the purchase was successful,
         * then the sku parameter specifies which item was purchased. If the purchase failed,
         * the sku and extraData parameters may or may not be null, depending on how far the purchase
         * process went.
         * @param requestCode
         * @param purchaseOrder
         * @param purchase
         */
        void onPurchaseFinished(int requestCode, PurchaseOrderType purchaseOrder, ProductPurchaseType purchase);
        void onPurchaseFailed(int requestCode, PurchaseOrderType purchaseOrder, BillingExceptionType billingException);
    }
}
