package com.tradehero.th.billing.samsung.purchasefetch;

import com.tradehero.common.billing.samsung.SamsungSKU;
import com.tradehero.common.billing.samsung.purchasefetch.SamsungPurchaseFetcherRx;
import com.tradehero.th.billing.purchasefetch.THPurchaseFetcherRx;
import com.tradehero.th.billing.samsung.THSamsungOrderId;
import com.tradehero.th.billing.samsung.THSamsungPurchase;

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
