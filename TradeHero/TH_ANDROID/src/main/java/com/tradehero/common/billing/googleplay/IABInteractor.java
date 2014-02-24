package com.tradehero.common.billing.googleplay;

import com.tradehero.common.billing.BillingInteractor;
import com.tradehero.common.billing.BillingLogicHolder;
import com.tradehero.common.billing.BillingInventoryFetcher;
import com.tradehero.common.billing.BillingPurchaser;
import com.tradehero.common.billing.ProductIdentifierFetcher;
import com.tradehero.common.billing.ProductIdentifierFetcherHolder;
import com.tradehero.common.billing.googleplay.exception.IABException;

/** Created with IntelliJ IDEA. User: xavier Date: 11/8/13 Time: 11:06 AM To change this template use File | Settings | File Templates. */
public interface IABInteractor<
        IABSKUType extends IABSKU,
        ProductIdentifierFetcherHolderType extends ProductIdentifierFetcherHolder<
                IABSKUType, OnProductIdentifierFetchedListenerType,
                IABExceptionType>,
        OnProductIdentifierFetchedListenerType extends ProductIdentifierFetcher.OnProductIdentifierFetchedListener<IABSKUType, IABExceptionType>,
        IABProductDetailType extends IABProductDetail<IABSKUType>,
        InventoryFetchedListenerType extends BillingInventoryFetcher.OnInventoryFetchedListener<IABSKUType, IABProductDetailType, IABExceptionType>,
        IABPurchaseOrderType extends IABPurchaseOrder<IABSKUType>,
        IABOrderIdType extends IABOrderId,
        IABPurchaseType extends IABPurchase<IABSKUType, IABOrderIdType>,
        BillingPurchaseFinishedListenerType extends BillingPurchaser.OnPurchaseFinishedListener<
                IABSKUType,
                IABPurchaseOrderType,
                IABOrderIdType,
                IABPurchaseType,
                IABExceptionType>,
        IABActorType extends BillingLogicHolder<
                        IABSKUType,
                        IABProductDetailType,
                        InventoryFetchedListenerType,
                        IABPurchaseOrderType,
                        IABOrderIdType,
                        IABPurchaseType,
                        BillingPurchaseFinishedListenerType,
                        IABExceptionType>,
        IABExceptionType extends IABException>
    extends BillingInteractor<
            IABSKUType,
            IABProductDetailType,
            InventoryFetchedListenerType,
            IABPurchaseOrderType,
            IABOrderIdType,
            IABPurchaseType,
            BillingPurchaseFinishedListenerType,
            IABActorType,
            IABExceptionType>
{
}
