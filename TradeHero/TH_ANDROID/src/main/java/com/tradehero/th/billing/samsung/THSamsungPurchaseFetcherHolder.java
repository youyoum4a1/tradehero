package com.tradehero.th.billing.samsung;

import com.tradehero.common.billing.samsung.SamsungPurchaseFetcherHolder;
import com.tradehero.common.billing.samsung.SamsungSKU;
import com.tradehero.common.billing.samsung.exception.SamsungException;
import com.tradehero.th.billing.THPurchaseFetcherHolder;

/** Created with IntelliJ IDEA. User: xavier Date: 11/8/13 Time: 11:06 AM To change this template use File | Settings | File Templates. */
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
