package com.tradehero.common.billing.googleplay;

import com.tradehero.common.billing.BillingActorInventoryFetcher;
import com.tradehero.common.billing.BillingInventoryFetcher;
import com.tradehero.common.billing.googleplay.exception.IABException;

/** Created with IntelliJ IDEA. User: xavier Date: 11/8/13 Time: 11:06 AM To change this template use File | Settings | File Templates. */
public interface IABActorInventoryFetcher<
        IABSKUType extends IABSKU,
        IABProductDetailsType extends IABProductDetail<IABSKUType>,
        InventoryFetchedListenerType extends BillingInventoryFetcher.OnInventoryFetchedListener<IABSKUType, IABProductDetailsType, IABExceptionType>,
        IABExceptionType extends IABException>
    extends BillingActorInventoryFetcher<
            IABSKUType,
            IABProductDetailsType,
            InventoryFetchedListenerType,
            IABExceptionType>
{
}
