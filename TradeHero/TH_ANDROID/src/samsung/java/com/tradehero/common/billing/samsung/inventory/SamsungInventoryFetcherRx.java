package com.tradehero.common.billing.samsung.inventory;

import com.tradehero.common.billing.inventory.BillingInventoryFetcherRx;
import com.tradehero.common.billing.samsung.SamsungProductDetail;
import com.tradehero.common.billing.samsung.SamsungSKU;

public interface SamsungInventoryFetcherRx<
        SamsungSKUType extends SamsungSKU,
        SamsungProductDetailsType extends SamsungProductDetail<SamsungSKUType>>
        extends BillingInventoryFetcherRx<
        SamsungSKUType,
        SamsungProductDetailsType>
{
}
