package com.androidth.general.common.billing.samsung.inventory;

import com.androidth.general.common.billing.inventory.BillingInventoryFetcherHolderRx;
import com.androidth.general.common.billing.samsung.SamsungProductDetail;
import com.androidth.general.common.billing.samsung.SamsungSKU;

public interface SamsungInventoryFetcherHolderRx<
        SamsungSKUType extends SamsungSKU,
        SamsungProductDetailType extends SamsungProductDetail<SamsungSKUType>>
        extends BillingInventoryFetcherHolderRx<
        SamsungSKUType,
        SamsungProductDetailType>
{
}
