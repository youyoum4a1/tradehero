package com.tradehero.common.billing.googleplay.inventory;

import com.tradehero.common.billing.googleplay.IABProductDetail;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.inventory.BillingInventoryFetcherRx;

public interface IABInventoryFetcherRx<
        IABSKUType extends IABSKU,
        IABProductDetailsType extends IABProductDetail<IABSKUType>>
    extends BillingInventoryFetcherRx<
            IABSKUType,
            IABProductDetailsType>
{
}
