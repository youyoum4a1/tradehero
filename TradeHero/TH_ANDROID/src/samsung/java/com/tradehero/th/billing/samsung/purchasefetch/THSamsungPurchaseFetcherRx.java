package com.androidth.general.billing.samsung.purchasefetch;

import com.androidth.general.common.billing.samsung.SamsungSKU;
import com.androidth.general.common.billing.samsung.purchasefetch.SamsungPurchaseFetcherRx;
import com.androidth.general.billing.purchasefetch.THPurchaseFetcherRx;
import com.androidth.general.billing.samsung.THSamsungOrderId;
import com.androidth.general.billing.samsung.THSamsungPurchase;

public interface THSamsungPurchaseFetcherRx
        extends
        SamsungPurchaseFetcherRx<
                SamsungSKU,
                THSamsungOrderId,
                THSamsungPurchase>,
        THPurchaseFetcherRx<
                SamsungSKU,
                THSamsungOrderId,
                THSamsungPurchase>
{
}
