package com.tradehero.th.billing.samsung;

import com.tradehero.common.billing.samsung.SamsungInventoryFetcherHolder;
import com.tradehero.common.billing.samsung.SamsungSKU;
import com.tradehero.common.billing.samsung.exception.SamsungException;
import com.tradehero.th.billing.THInventoryFetcherHolder;

public interface THSamsungInventoryFetcherHolder
        extends
        SamsungInventoryFetcherHolder<
                SamsungSKU,
                THSamsungProductDetail,
                SamsungException>,
        THInventoryFetcherHolder<
                SamsungSKU,
                THSamsungProductDetail,
                SamsungException>
{
}
