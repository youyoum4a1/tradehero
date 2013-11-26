package com.tradehero.common.billing;

/** Created with IntelliJ IDEA. User: xavier Date: 11/8/13 Time: 11:06 AM To change this template use File | Settings | File Templates. */
public interface BillingActorUser<
        ProductIdentifierType extends ProductIdentifier,
        ProductDetailsType extends ProductDetails<ProductIdentifierType>,
        InventoryFetchedListenerType extends InventoryFetcher.OnInventoryFetchedListener<
                ProductIdentifierType,
                ProductDetailsType,
                ExceptionType>,
        PurchaseOrderType extends PurchaseOrder<ProductIdentifierType>,
        OrderIdType extends OrderId,
        ProductPurchaseType extends ProductPurchase<
                ProductIdentifierType,
                OrderIdType>,
        BillingPurchaseFinishedListenerType extends BillingPurchaser.OnPurchaseFinishedListener<
                ProductIdentifierType,
                PurchaseOrderType,
                OrderIdType,
                ProductPurchaseType,
                ExceptionType>,
        BillingActorType extends BillingActor<
                ProductIdentifierType,
                ProductDetailsType,
                InventoryFetchedListenerType,
                PurchaseOrderType,
                OrderIdType,
                ProductPurchaseType,
                BillingPurchaseFinishedListenerType,
                ExceptionType>,
        ExceptionType extends Exception>
{
    void setBillingActor(BillingActorType billingActor);
    BillingActorType getBillingActor();
}
