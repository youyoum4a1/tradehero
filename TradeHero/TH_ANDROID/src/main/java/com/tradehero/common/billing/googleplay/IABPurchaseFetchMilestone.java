package com.tradehero.common.billing.googleplay;

import com.tradehero.common.billing.BillingPurchaseFetcher;
import com.tradehero.common.billing.PurchaseFetchMilestone;
import com.tradehero.common.billing.googleplay.exception.IABException;

/** Created with IntelliJ IDEA. User: xavier Date: 11/26/13 Time: 11:36 AM To change this template use File | Settings | File Templates. */
abstract public class IABPurchaseFetchMilestone<
        IABSKUType extends IABSKU,
        IABOrderIdType extends IABOrderId,
        IABPurchaseType extends IABPurchase<IABSKUType, IABOrderIdType>,
        IABPurchaseFetchedListenerType extends BillingPurchaseFetcher.OnPurchaseFetchedListener<
                IABSKUType,
                IABOrderIdType,
                IABPurchaseType,
                IABException>>
        extends PurchaseFetchMilestone<
        IABSKUType,
        IABOrderIdType,
        IABPurchaseType,
        IABPurchaseFetchedListenerType,
        IABException>
{
    public static final String TAG = IABPurchaseFetchMilestone.class.getSimpleName();

    public IABPurchaseFetchMilestone(IABPurchaseFetcherHolder<IABSKUType, IABOrderIdType, IABPurchaseType, IABPurchaseFetchedListenerType, IABException> purchaseFetcherHolder)
    {
        super(purchaseFetcherHolder);
    }
}
