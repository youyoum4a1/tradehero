package com.tradehero.th.billing.samsung.inventory;

import com.tradehero.common.billing.samsung.SamsungSKU;
import com.tradehero.common.billing.samsung.inventory.SamsungInventoryFetcherHolderRx;
import com.tradehero.th.billing.inventory.THInventoryFetcherHolderRx;
import com.tradehero.th.billing.samsung.THSamsungProductDetail;

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
