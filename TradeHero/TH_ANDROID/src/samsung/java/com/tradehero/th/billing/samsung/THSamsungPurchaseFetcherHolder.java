package com.tradehero.th.billing.samsung;

import com.tradehero.common.billing.samsung.SamsungPurchaseFetcherHolder;
import com.tradehero.common.billing.samsung.SamsungSKU;
import com.tradehero.common.billing.samsung.exception.SamsungException;
import com.tradehero.th.billing.THPurchaseFetcherHolder;

public interface THSamsungPurchaseFetcherHolder
        extends
        SamsungPurchaseFetcherHolder<
                SamsungSKU,
                THSamsungOrderId,
                THSamsungPurchase,
                SamsungException>,
        THPurchaseFetcherHolder<
                SamsungSKU,
                THSamsungOrderId,
                THSamsungPurchase,
                SamsungException>
{
}
