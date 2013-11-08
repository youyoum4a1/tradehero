package com.tradehero.common.billing;

import com.tradehero.th.billing.googleplay.THSKUDetails;

/** Created with IntelliJ IDEA. User: xavier Date: 11/8/13 Time: 11:06 AM To change this template use File | Settings | File Templates. */
public interface BillingActor<ProductIdentifierType extends ProductIdentifier,
                        ProductDetailsType extends ProductDetails<ProductIdentifierType>,
                        ExceptionType extends Exception,
                        OrderIdType extends OrderId,
                        ProductPurchaseType extends ProductPurchase<OrderIdType, ProductIdentifierType>,
                        BillingPurchaseHandlerType extends BillingPurchaseHandler<OrderIdType, ProductIdentifierType, ProductPurchaseType, ExceptionType>>
{
    boolean isBillingAvailable();
    boolean isInventoryReady();
    boolean hadErrorLoadingInventory();
    void launchSkuInventorySequence();
    int launchPurchaseSequence(BillingPurchaseHandlerType billingPurchaseHandler, ProductDetailsType productDetails);
    int launchPurchaseSequence(BillingPurchaseHandlerType billingPurchaseHandler, ProductDetailsType productDetails, Object extraData);
}
