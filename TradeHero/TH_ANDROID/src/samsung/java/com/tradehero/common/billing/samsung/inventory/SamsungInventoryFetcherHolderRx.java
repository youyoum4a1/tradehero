package com.tradehero.common.billing.samsung.inventory;

import com.tradehero.common.billing.inventory.BillingInventoryFetcherHolderRx;
import com.tradehero.common.billing.samsung.SamsungProductDetail;
import com.tradehero.common.billing.samsung.SamsungSKU;

public interface SamsungInventoryFetcherHolderRx<
        SamsungSKUType extends SamsungSKU,
        SamsungProductDetailType extends SamsungProductDetail<SamsungSKUType>>
        extends BillingInventoryFetcherHolderRx<
        SamsungSKUType,
        SamsungProductDetailType>
{
}
