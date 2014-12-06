package com.tradehero.th.billing.samsung.purchasefetch;

import com.tradehero.common.billing.samsung.SamsungSKU;
import com.tradehero.common.billing.samsung.purchasefetch.SamsungPurchaseFetcherHolderRx;
import com.tradehero.th.billing.purchasefetch.THPurchaseFetcherHolderRx;
import com.tradehero.th.billing.samsung.THSamsungOrderId;
import com.tradehero.th.billing.samsung.THSamsungPurchase;

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
