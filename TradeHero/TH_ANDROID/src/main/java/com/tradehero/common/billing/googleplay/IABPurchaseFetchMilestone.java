package com.tradehero.common.billing.googleplay;

import com.tradehero.common.billing.BillingPurchaseFetcherHolder;
import com.tradehero.common.billing.PurchaseFetchMilestone;
import com.tradehero.common.billing.googleplay.exception.IABException;

/** Created with IntelliJ IDEA. User: xavier Date: 11/26/13 Time: 11:36 AM To change this template use File | Settings | File Templates. */
abstract public class IABPurchaseFetchMilestone<
        IABSKUType extends IABSKU,
        IABOrderIdType extends IABOrderId,
        IABPurchaseType extends IABPurchase<IABSKUType, IABOrderIdType>>
        extends PurchaseFetchMilestone<
        IABSKUType,
        IABOrderIdType,
        IABPurchaseType,
        IABException>
{
    public static final String TAG = IABPurchaseFetchMilestone.class.getSimpleName();

    public IABPurchaseFetchMilestone(BillingPurchaseFetcherHolder<IABSKUType, IABOrderIdType, IABPurchaseType, IABException> purchaseFetcherHolder)
    {
        super(purchaseFetcherHolder);
    }
}
