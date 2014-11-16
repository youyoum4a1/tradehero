package com.tradehero.common.billing.googleplay.purchasefetch;

import com.tradehero.common.billing.googleplay.IABOrderId;
import com.tradehero.common.billing.googleplay.IABPurchase;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.purchasefetch.BillingPurchaseFetcherHolderRx;

public interface IABPurchaseFetcherHolderRx<
        IABSKUType extends IABSKU,
        IABOrderIdType extends IABOrderId,
        IABPurchaseType extends IABPurchase<IABSKUType, IABOrderIdType>>
        extends BillingPurchaseFetcherHolderRx<
        IABSKUType,
        IABOrderIdType,
        IABPurchaseType>
{
}
