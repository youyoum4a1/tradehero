package com.androidth.general.common.billing.samsung.inventory;

import com.androidth.general.common.billing.inventory.BillingInventoryFetcherRx;
import com.androidth.general.common.billing.samsung.SamsungProductDetail;
import com.androidth.general.common.billing.samsung.SamsungSKU;

public interface SamsungInventoryFetcherRx<
        SamsungSKUType extends SamsungSKU,
        SamsungProductDetailsType extends SamsungProductDetail<SamsungSKUType>>
        extends BillingInventoryFetcherRx<
        SamsungSKUType,
        SamsungProductDetailsType>
{
}
