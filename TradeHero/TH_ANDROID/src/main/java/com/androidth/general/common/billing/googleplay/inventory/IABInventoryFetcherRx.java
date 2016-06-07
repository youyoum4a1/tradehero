package com.androidth.general.common.billing.googleplay.inventory;

import com.androidth.general.common.billing.googleplay.IABProductDetail;
import com.androidth.general.common.billing.googleplay.IABSKU;
import com.androidth.general.common.billing.inventory.BillingInventoryFetcherRx;

public interface IABInventoryFetcherRx<
        IABSKUType extends IABSKU,
        IABProductDetailsType extends IABProductDetail<IABSKUType>>
    extends BillingInventoryFetcherRx<
            IABSKUType,
            IABProductDetailsType>
{
}
