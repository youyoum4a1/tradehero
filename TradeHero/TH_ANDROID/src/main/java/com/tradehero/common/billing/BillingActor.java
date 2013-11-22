package com.tradehero.common.billing;

import com.tradehero.th.billing.googleplay.THSKUDetails;

/** Created with IntelliJ IDEA. User: xavier Date: 11/8/13 Time: 11:06 AM To change this template use File | Settings | File Templates. */
public interface BillingActor<
                        ProductIdentifierType extends ProductIdentifier,
                        PurchaseOrderType extends PurchaseOrder<ProductIdentifierType>,
                        OrderIdType extends OrderId,
                        ProductPurchaseType extends ProductPurchase<ProductIdentifierType, OrderIdType>,
                        BillingPurchaseHandlerType extends BillingPurchaseHandler<
                                                        ProductIdentifierType,
                                                        OrderIdType,
                                                        ProductPurchaseType,
                                                        ExceptionType>,
                        ExceptionType extends Exception>
{
    boolean isBillingAvailable();
    void launchSkuInventorySequence();
    boolean isInventoryReady();
    boolean hadErrorLoadingInventory();
    int registerBillingPurchaseHandler(BillingPurchaseHandlerType billingPurchaseHandler);
    void launchPurchaseSequence(int requestCode, PurchaseOrderType purchaseOrder);
}
