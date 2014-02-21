package com.tradehero.common.billing;

import com.tradehero.common.billing.exception.BillingException;

/** Created with IntelliJ IDEA. User: xavier Date: 11/8/13 Time: 11:06 AM To change this template use File | Settings | File Templates. */
public interface BillingActorUser<
        ProductIdentifierType extends ProductIdentifier,
        OnProductIdentifierFetchedListenerType extends ProductIdentifierFetcher.OnProductIdentifierFetchedListener<ProductIdentifierType, BillingExceptionType>,
        ProductDetailType extends ProductDetail<ProductIdentifierType>,
        InventoryFetchedListenerType extends BillingInventoryFetcher.OnInventoryFetchedListener<
                ProductIdentifierType,
                ProductDetailType,
                BillingExceptionType>,
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
                BillingExceptionType>,
        BillingActorType extends BillingLogicHolder<
                        ProductIdentifierType,
                        OnProductIdentifierFetchedListenerType,
                        ProductDetailType,
                        InventoryFetchedListenerType,
                        PurchaseOrderType,
                        OrderIdType,
                        ProductPurchaseType,
                        BillingPurchaseFinishedListenerType,
                        BillingExceptionType>,
        BillingExceptionType extends BillingException>
{
    void setBillingActor(BillingActorType billingActor);
    BillingActorType getBillingActor();
}
