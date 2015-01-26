package com.tradehero.th.billing.samsung.inventory;

import com.tradehero.common.billing.samsung.SamsungSKU;
import com.tradehero.common.billing.samsung.inventory.SamsungInventoryFetcherRx;
import com.tradehero.th.billing.inventory.THInventoryFetcherRx;
import com.tradehero.th.billing.samsung.THSamsungProductDetail;

public interface THSamsungInventoryFetcherRx
        extends
        SamsungInventoryFetcherRx<
                SamsungSKU,
                THSamsungProductDetail>,
        THInventoryFetcherRx<
                SamsungSKU,
                THSamsungProductDetail>
{
}
