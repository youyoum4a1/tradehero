package com.ayondo.academy.billing.samsung.inventory;

import com.tradehero.common.billing.samsung.SamsungSKU;
import com.tradehero.common.billing.samsung.inventory.SamsungInventoryFetcherHolderRx;
import com.ayondo.academy.billing.inventory.THInventoryFetcherHolderRx;
import com.ayondo.academy.billing.samsung.THSamsungProductDetail;

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
