package com.tradehero.common.billing;

import com.tradehero.common.billing.exception.BillingException;

/** Created with IntelliJ IDEA. User: xavier Date: 11/8/13 Time: 11:06 AM To change this template use File | Settings | File Templates. */
public interface BillingLogicHolder<
        ProductIdentifierType extends ProductIdentifier,
        OnProductIdentifierFetchedListenerType extends ProductIdentifierFetcher.OnProductIdentifierFetchedListener<ProductIdentifierType, BillingExceptionType>,
        ProductDetailType extends ProductDetail<ProductIdentifierType>,
        InventoryFetchedListenerType extends BillingInventoryFetcher.OnInventoryFetchedListener<ProductIdentifierType, ProductDetailType, BillingExceptionType>,
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
    extends
        ProductIdentifierFetcherHolder<
                ProductIdentifierType,
                OnProductIdentifierFetchedListenerType,
                BillingExceptionType>,
        BillingInventoryFetcherHolder<
                        ProductIdentifierType,
                        ProductDetailType,
                        InventoryFetchedListenerType,
                BillingExceptionType>,
        BillingPurchaserHolder<
                        ProductIdentifierType,
                        PurchaseOrderType,
                        OrderIdType,
                        ProductPurchaseType,
                        BillingPurchaseFinishedListenerType,
                BillingExceptionType>
{
}
