package com.tradehero.th.billing.samsung;

import com.tradehero.common.billing.samsung.SamsungInventoryFetcherHolder;
import com.tradehero.common.billing.samsung.SamsungSKU;
import com.tradehero.common.billing.samsung.exception.SamsungException;
import com.tradehero.th.billing.THInventoryFetcherHolder;

/**
 * Created by xavier on 3/27/14.
 */
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
