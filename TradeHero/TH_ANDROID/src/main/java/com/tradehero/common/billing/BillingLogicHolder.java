package com.tradehero.common.billing;

import com.tradehero.common.billing.exception.BillingException;
import com.tradehero.common.billing.googleplay.IABProductIdentifierFetcher;
import com.tradehero.common.billing.googleplay.exception.IABException;

/** Created with IntelliJ IDEA. User: xavier Date: 11/8/13 Time: 11:06 AM To change this template use File | Settings | File Templates. */
public interface BillingLogicHolder<
        ProductIdentifierType extends ProductIdentifier,
        ProductIdentifierFetcherHolderType extends ProductIdentifierFetcherHolder<
                ProductIdentifierType, OnProductIdentifierFetchedListenerType,
                BillingExceptionType>,
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
    boolean isBillingAvailable();
    void forgetRequestCode(int requestCode);
    void onDestroy();
}
