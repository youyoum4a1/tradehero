package com.androidth.general.billing.googleplay.inventory;

import com.androidth.general.common.billing.googleplay.IABSKU;
import com.androidth.general.common.billing.googleplay.inventory.IABInventoryFetcherHolderRx;
import com.androidth.general.billing.googleplay.THIABProductDetail;
import com.androidth.general.billing.inventory.THInventoryFetcherHolderRx;

public interface THIABInventoryFetcherHolderRx
        extends IABInventoryFetcherHolderRx<
        IABSKU,
        THIABProductDetail>,
        THInventoryFetcherHolderRx<
                IABSKU,
                THIABProductDetail>
{
}
