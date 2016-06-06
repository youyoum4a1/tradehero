package com.androidth.general.billing.samsung.purchasefetch;

import com.androidth.general.common.billing.samsung.SamsungSKU;
import com.androidth.general.common.billing.samsung.purchasefetch.SamsungPurchaseFetcherHolderRx;
import com.androidth.general.billing.purchasefetch.THPurchaseFetcherHolderRx;
import com.androidth.general.billing.samsung.THSamsungOrderId;
import com.androidth.general.billing.samsung.THSamsungPurchase;

public interface THSamsungPurchaseFetcherHolderRx
        extends
        SamsungPurchaseFetcherHolderRx<
                SamsungSKU,
                THSamsungOrderId,
                THSamsungPurchase>,
        THPurchaseFetcherHolderRx<
                SamsungSKU,
                THSamsungOrderId,
                THSamsungPurchase>
{
}
