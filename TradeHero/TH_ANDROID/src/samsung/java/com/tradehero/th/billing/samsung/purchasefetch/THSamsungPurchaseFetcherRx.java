package com.ayondo.academy.billing.samsung.purchasefetch;

import com.tradehero.common.billing.samsung.SamsungSKU;
import com.tradehero.common.billing.samsung.purchasefetch.SamsungPurchaseFetcherRx;
import com.ayondo.academy.billing.purchasefetch.THPurchaseFetcherRx;
import com.ayondo.academy.billing.samsung.THSamsungOrderId;
import com.ayondo.academy.billing.samsung.THSamsungPurchase;

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
