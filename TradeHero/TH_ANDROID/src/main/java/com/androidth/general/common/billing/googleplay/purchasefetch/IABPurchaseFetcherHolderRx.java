package com.androidth.general.common.billing.googleplay.purchasefetch;

import com.androidth.general.common.billing.googleplay.IABOrderId;
import com.androidth.general.common.billing.googleplay.IABPurchase;
import com.androidth.general.common.billing.googleplay.IABSKU;
import com.androidth.general.common.billing.purchasefetch.BillingPurchaseFetcherHolderRx;

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
