package com.tradehero.th.billing.googleplay.inventory;

import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.inventory.IABInventoryFetcherHolderRx;
import com.tradehero.th.billing.googleplay.THIABProductDetail;
import com.tradehero.th.billing.inventory.THInventoryFetcherHolderRx;

public interface THIABInventoryFetcherHolderRx
        extends IABInventoryFetcherHolderRx<
        IABSKU,
        THIABProductDetail>,
        THInventoryFetcherHolderRx<
                IABSKU,
                THIABProductDetail>
{
}
