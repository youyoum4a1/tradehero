package com.androidth.general.billing.googleplay.inventory;

import com.androidth.general.common.billing.googleplay.IABSKU;
import com.androidth.general.common.billing.googleplay.inventory.IABInventoryFetcherRx;
import com.androidth.general.billing.googleplay.THIABProductDetail;
import com.androidth.general.billing.inventory.THInventoryFetcherRx;

public interface THIABInventoryFetcherRx
        extends IABInventoryFetcherRx<
        IABSKU,
        THIABProductDetail>,
        THInventoryFetcherRx<
                IABSKU,
                THIABProductDetail>
{
}
