package com.ayondo.academy.billing.googleplay.inventory;

import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.inventory.IABInventoryFetcherRx;
import com.ayondo.academy.billing.googleplay.THIABProductDetail;
import com.ayondo.academy.billing.inventory.THInventoryFetcherRx;

public interface THIABInventoryFetcherRx
        extends IABInventoryFetcherRx<
        IABSKU,
        THIABProductDetail>,
        THInventoryFetcherRx<
                IABSKU,
                THIABProductDetail>
{
}
