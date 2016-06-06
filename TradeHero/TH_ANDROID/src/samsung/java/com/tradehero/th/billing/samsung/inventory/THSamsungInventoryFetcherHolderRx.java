package com.androidth.general.billing.samsung.inventory;

import com.androidth.general.common.billing.samsung.SamsungSKU;
import com.androidth.general.common.billing.samsung.inventory.SamsungInventoryFetcherHolderRx;
import com.androidth.general.billing.inventory.THInventoryFetcherHolderRx;
import com.androidth.general.billing.samsung.THSamsungProductDetail;

public interface THSamsungInventoryFetcherHolderRx
        extends
        SamsungInventoryFetcherHolderRx<
                SamsungSKU,
                THSamsungProductDetail>,
        THInventoryFetcherHolderRx<
                SamsungSKU,
                THSamsungProductDetail>
{
}
