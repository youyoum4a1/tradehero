package com.androidth.general.common.billing.samsung.purchasefetch;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import com.androidth.general.common.billing.purchasefetch.BaseBillingPurchaseFetcherHolderRx;
import com.androidth.general.common.billing.samsung.SamsungOrderId;
import com.androidth.general.common.billing.samsung.SamsungPurchase;
import com.androidth.general.common.billing.samsung.SamsungSKU;

abstract public class BaseSamsungPurchaseFetcherHolderRx<
        SamsungSKUType extends SamsungSKU,
        SamsungOrderIdType extends SamsungOrderId,
        SamsungPurchaseType extends SamsungPurchase<SamsungSKUType, SamsungOrderIdType>>
        extends BaseBillingPurchaseFetcherHolderRx<
        SamsungSKUType,
        SamsungOrderIdType,
        SamsungPurchaseType>
        implements SamsungPurchaseFetcherHolderRx<
        SamsungSKUType,
        SamsungOrderIdType,
        SamsungPurchaseType>
{
    //<editor-fold desc="Constructors">
    public BaseSamsungPurchaseFetcherHolderRx()
    {
        super();
    }
    //</editor-fold>

    @NonNull @Override abstract protected SamsungPurchaseFetcherRx<SamsungSKUType, SamsungOrderIdType, SamsungPurchaseType> createFetcher(
            int requestCode);

    @Override public void onActivityResult(@NonNull Activity activity, int requestCode, int resultCode, Intent data)
    {
        // Nothing to do
    }
}
