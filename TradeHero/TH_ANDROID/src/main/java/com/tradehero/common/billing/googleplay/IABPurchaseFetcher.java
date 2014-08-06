package com.tradehero.common.billing.googleplay;

import com.tradehero.common.billing.BillingPurchaseFetcher;
import com.tradehero.common.billing.googleplay.exception.IABException;

public interface IABPurchaseFetcher<
        IABSKUType extends IABSKU,
        IABOrderIdType extends IABOrderId,
        IABPurchaseType extends IABPurchase<IABSKUType, IABOrderIdType>,
        IABExceptionType extends IABException>
    extends BillingPurchaseFetcher<
            IABSKUType,
            IABOrderIdType,
            IABPurchaseType,
            IABExceptionType>
{
}
