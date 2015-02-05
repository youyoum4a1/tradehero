package com.tradehero.common.billing.googleplay.purchasefetch;

import android.support.annotation.NonNull;
import com.tradehero.common.billing.googleplay.IABOrderId;
import com.tradehero.common.billing.googleplay.IABPurchase;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.purchasefetch.BaseBillingPurchaseFetcherHolderRx;
import com.tradehero.common.billing.purchasefetch.BillingPurchaseFetcherRx;

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

    @Override public void onDestroy()
    {
        for (BillingPurchaseFetcherRx<IABSKUType, IABOrderIdType, IABPurchaseType> actor : actors.values())
        {
            ((IABPurchaseFetcherRx<IABSKUType, IABOrderIdType, IABPurchaseType>) actor).onDestroy();
        }
        super.onDestroy();
    }

    @Override public void forgetRequestCode(int requestCode)
    {
        IABPurchaseFetcherRx<IABSKUType, IABOrderIdType, IABPurchaseType> actor = (IABPurchaseFetcherRx<IABSKUType, IABOrderIdType, IABPurchaseType>) actors.get(requestCode);
        if (actor != null)
        {
            actor.onDestroy();
        }
        super.forgetRequestCode(requestCode);
    }
}
