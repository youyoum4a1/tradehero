package com.tradehero.common.billing.googleplay;

import com.tradehero.common.billing.BillingPurchaseFetcherHolder;
import com.tradehero.common.billing.googleplay.exception.IABException;

public interface IABPurchaseFetcherHolder<
        IABSKUType extends IABSKU,
        IABOrderIdType extends IABOrderId,
        IABPurchaseType extends IABPurchase<IABSKUType, IABOrderIdType>,
        IABExceptionType extends IABException>
    extends BillingPurchaseFetcherHolder<
        IABSKUType,
        IABOrderIdType,
        IABPurchaseType,
        IABExceptionType>
{
}
