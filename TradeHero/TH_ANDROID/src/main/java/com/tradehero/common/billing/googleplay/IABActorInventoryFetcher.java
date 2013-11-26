package com.tradehero.common.billing.googleplay;

import com.tradehero.common.billing.BillingActor;
import com.tradehero.common.billing.BillingActorInventoryFetcher;
import com.tradehero.common.billing.BillingPurchaser;
import com.tradehero.common.billing.InventoryFetcher;
import com.tradehero.common.billing.googleplay.exceptions.IABException;

/** Created with IntelliJ IDEA. User: xavier Date: 11/8/13 Time: 11:06 AM To change this template use File | Settings | File Templates. */
public interface IABActorInventoryFetcher<
        IABSKUType extends IABSKU,
        IABProductDetailsType extends IABProductDetails<IABSKUType>,
        InventoryFetchedListenerType extends InventoryFetcher.OnInventoryFetchedListener<IABSKUType, IABProductDetailsType, IABExceptionType>,
        IABExceptionType extends IABException>
    extends BillingActorInventoryFetcher<
            IABSKUType,
            IABProductDetailsType,
            InventoryFetchedListenerType,
            IABExceptionType>
{
}
