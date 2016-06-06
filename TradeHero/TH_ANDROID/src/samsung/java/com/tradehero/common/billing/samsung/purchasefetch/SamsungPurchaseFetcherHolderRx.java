package com.androidth.general.common.billing.samsung.purchasefetch;

import com.androidth.general.common.billing.purchasefetch.BillingPurchaseFetcherHolderRx;
import com.androidth.general.common.billing.samsung.SamsungOrderId;
import com.androidth.general.common.billing.samsung.SamsungPurchase;
import com.androidth.general.common.billing.samsung.SamsungSKU;

public interface SamsungPurchaseFetcherHolderRx<
        SamsungSKUType extends SamsungSKU,
        SamsungOrderIdType extends SamsungOrderId,
        SamsungPurchaseType extends SamsungPurchase<SamsungSKUType, SamsungOrderIdType>>
        extends BillingPurchaseFetcherHolderRx<
        SamsungSKUType,
        SamsungOrderIdType,
        SamsungPurchaseType>
{
}
