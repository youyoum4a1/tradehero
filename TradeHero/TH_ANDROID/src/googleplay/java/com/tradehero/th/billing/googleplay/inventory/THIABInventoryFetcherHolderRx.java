package com.ayondo.academy.billing.googleplay.inventory;

import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.inventory.IABInventoryFetcherHolderRx;
import com.ayondo.academy.billing.googleplay.THIABProductDetail;
import com.ayondo.academy.billing.inventory.THInventoryFetcherHolderRx;

public interface THIABInventoryFetcherHolderRx
        extends IABInventoryFetcherHolderRx<
        IABSKU,
        THIABProductDetail>,
        THInventoryFetcherHolderRx<
                IABSKU,
                THIABProductDetail>
{
}
