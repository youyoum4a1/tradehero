package com.androidth.general.billing.samsung.inventory;

import com.androidth.general.common.billing.samsung.SamsungSKU;
import com.androidth.general.common.billing.samsung.inventory.SamsungInventoryFetcherRx;
import com.androidth.general.billing.inventory.THInventoryFetcherRx;
import com.androidth.general.billing.samsung.THSamsungProductDetail;

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
