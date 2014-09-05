package com.tradehero.common.billing.samsung;

import com.tradehero.common.billing.BillingInventoryFetcherHolder;
import com.tradehero.common.billing.samsung.exception.SamsungException;

public interface SamsungInventoryFetcherHolder<
        SamsungSKUType extends SamsungSKU,
        SamsungProductDetailType extends SamsungProductDetail<SamsungSKUType>,
        SamsungExceptionType extends SamsungException>
    extends BillingInventoryFetcherHolder<
        SamsungSKUType,
        SamsungProductDetailType,
        SamsungExceptionType>
{
}
