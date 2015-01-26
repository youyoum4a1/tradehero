package com.tradehero.th.billing.googleplay.inventory;

import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.inventory.IABInventoryFetcherRx;
import com.tradehero.th.billing.googleplay.THIABProductDetail;
import com.tradehero.th.billing.inventory.THInventoryFetcherRx;

public interface THIABInventoryFetcherRx
        extends IABInventoryFetcherRx<
        IABSKU,
        THIABProductDetail>,
        THInventoryFetcherRx<
                IABSKU,
                THIABProductDetail>
{
}
