package com.tradehero.th.billing.samsung;

import com.tradehero.common.billing.samsung.SamsungPurchaseFetcher;
import com.tradehero.common.billing.samsung.SamsungSKU;
import com.tradehero.common.billing.samsung.exception.SamsungException;
import com.tradehero.th.billing.THPurchaseFetcher;

public interface THSamsungPurchaseFetcher
        extends
        SamsungPurchaseFetcher<
                SamsungSKU,
                THSamsungOrderId,
                THSamsungPurchase,
                SamsungException>,
        THPurchaseFetcher<
                SamsungSKU,
                THSamsungOrderId,
                THSamsungPurchase,
                SamsungException>
{
}
