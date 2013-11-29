package com.tradehero.common.billing.googleplay;

import com.tradehero.common.billing.BillingActor;
import com.tradehero.common.billing.BillingActorUser;
import com.tradehero.common.billing.BillingPurchaser;
import com.tradehero.common.billing.InventoryFetcher;
import com.tradehero.common.billing.googleplay.exceptions.IABException;

/** Created with IntelliJ IDEA. User: xavier Date: 11/8/13 Time: 11:06 AM To change this template use File | Settings | File Templates. */
public interface IABActorUser<
        IABSKUType extends IABSKU,
        IABProductDetailType extends IABProductDetail<IABSKUType>,
        InventoryFetchedListenerType extends InventoryFetcher.OnInventoryFetchedListener<IABSKUType, IABProductDetailType, IABExceptionType>,
        IABPurchaseOrderType extends IABPurchaseOrder<IABSKUType>,
        IABOrderIdType extends IABOrderId,
        IABPurchaseType extends IABPurchase<IABSKUType, IABOrderIdType>,
        BillingPurchaseFinishedListenerType extends BillingPurchaser.OnPurchaseFinishedListener<
                IABSKUType,
                IABPurchaseOrderType,
                IABOrderIdType,
                IABPurchaseType,
                IABExceptionType>,
        IABActorType extends BillingActor<
                IABSKUType,
                IABProductDetailType,
                InventoryFetchedListenerType,
                IABPurchaseOrderType,
                IABOrderIdType,
                IABPurchaseType,
                BillingPurchaseFinishedListenerType,
                IABExceptionType>,
        IABExceptionType extends IABException>
    extends BillingActorUser<
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
