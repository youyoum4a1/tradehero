package com.ayondo.academy.billing.samsung.inventory;

import com.tradehero.common.billing.samsung.SamsungSKU;
import com.tradehero.common.billing.samsung.inventory.SamsungInventoryFetcherRx;
import com.ayondo.academy.billing.inventory.THInventoryFetcherRx;
import com.ayondo.academy.billing.samsung.THSamsungProductDetail;

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
