package com.tradehero.th.billing.samsung;

import com.tradehero.common.billing.samsung.SamsungInventoryFetcher;
import com.tradehero.common.billing.samsung.SamsungSKU;
import com.tradehero.common.billing.samsung.exception.SamsungException;
import com.tradehero.th.billing.THInventoryFetcher;

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
