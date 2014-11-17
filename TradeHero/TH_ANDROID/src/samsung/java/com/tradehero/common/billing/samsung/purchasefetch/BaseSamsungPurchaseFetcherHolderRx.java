package com.tradehero.common.billing.samsung.purchasefetch;

import android.support.annotation.NonNull;
import com.tradehero.common.billing.purchasefetch.BaseBillingPurchaseFetcherHolderRx;
import com.tradehero.common.billing.samsung.SamsungOrderId;
import com.tradehero.common.billing.samsung.SamsungPurchase;
import com.tradehero.common.billing.samsung.SamsungSKU;

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
}
