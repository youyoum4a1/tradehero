package com.tradehero.th.billing.samsung;

import com.tradehero.common.billing.samsung.SamsungInventoryFetcher;
import com.tradehero.common.billing.samsung.SamsungSKU;
import com.tradehero.common.billing.samsung.exception.SamsungException;
import com.tradehero.th.billing.THInventoryFetcher;

/**
 * Created by xavier on 3/27/14.
 */
public interface THSamsungInventoryFetcher
        extends
        SamsungInventoryFetcher<
                SamsungSKU,
                THSamsungProductDetail,
                SamsungException>,
        THInventoryFetcher<
                SamsungSKU,
                THSamsungProductDetail,
                SamsungException>
{
}
