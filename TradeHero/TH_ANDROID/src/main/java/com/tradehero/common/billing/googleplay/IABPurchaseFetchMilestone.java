package com.tradehero.common.billing.googleplay;

import com.tradehero.common.billing.BillingPurchaseFetcherHolder;
import com.tradehero.common.billing.PurchaseFetchMilestone;
import com.tradehero.common.billing.googleplay.exception.IABException;

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
    public IABPurchaseFetchMilestone(BillingPurchaseFetcherHolder<IABSKUType, IABOrderIdType, IABPurchaseType, IABException> purchaseFetcherHolder)
    {
        super(purchaseFetcherHolder);
    }
}
