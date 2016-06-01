package com.ayondo.academy.billing.samsung.purchasefetch;

import com.tradehero.common.billing.samsung.SamsungSKU;
import com.tradehero.common.billing.samsung.purchasefetch.SamsungPurchaseFetcherHolderRx;
import com.ayondo.academy.billing.purchasefetch.THPurchaseFetcherHolderRx;
import com.ayondo.academy.billing.samsung.THSamsungOrderId;
import com.ayondo.academy.billing.samsung.THSamsungPurchase;

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
