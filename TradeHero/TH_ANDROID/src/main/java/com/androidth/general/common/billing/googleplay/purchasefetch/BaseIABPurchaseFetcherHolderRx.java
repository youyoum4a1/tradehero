package com.androidth.general.common.billing.googleplay.purchasefetch;

import android.support.annotation.NonNull;
import com.androidth.general.common.billing.googleplay.IABOrderId;
import com.androidth.general.common.billing.googleplay.IABPurchase;
import com.androidth.general.common.billing.googleplay.IABSKU;
import com.androidth.general.common.billing.purchasefetch.BaseBillingPurchaseFetcherHolderRx;

abstract public class BaseIABPurchaseFetcherHolderRx<
        IABSKUType extends IABSKU,
        IABOrderIdType extends IABOrderId,
        IABPurchaseType extends IABPurchase<IABSKUType, IABOrderIdType>>
        extends BaseBillingPurchaseFetcherHolderRx<
        IABSKUType,
        IABOrderIdType,
        IABPurchaseType>
        implements IABPurchaseFetcherHolderRx<
        IABSKUType,
        IABOrderIdType,
        IABPurchaseType>
{
    //<editor-fold desc="Constructors">
    public BaseIABPurchaseFetcherHolderRx()
    {
        super();
    }
    //</editor-fold>

    @NonNull @Override abstract protected IABPurchaseFetcherRx<IABSKUType, IABOrderIdType, IABPurchaseType> createFetcher(int requestCode);
}
