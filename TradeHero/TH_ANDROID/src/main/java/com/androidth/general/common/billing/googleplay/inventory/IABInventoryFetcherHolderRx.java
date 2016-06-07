package com.androidth.general.common.billing.googleplay.inventory;

import com.androidth.general.common.billing.googleplay.IABProductDetail;
import com.androidth.general.common.billing.googleplay.IABSKU;
import com.androidth.general.common.billing.inventory.BillingInventoryFetcherHolderRx;

public interface IABInventoryFetcherHolderRx<
        IABSKUType extends IABSKU,
        IABProductDetailsType extends IABProductDetail<IABSKUType>>
    extends BillingInventoryFetcherHolderRx<
                    IABSKUType,
                    IABProductDetailsType>
{
}
