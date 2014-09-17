package com.tradehero.common.billing.samsung;

import com.sec.android.iap.lib.listener.OnGetItemListener;
import com.tradehero.common.billing.BillingInventoryFetcher;
import com.tradehero.common.billing.samsung.exception.SamsungException;

public interface SamsungInventoryFetcher<
        SamsungSKUType extends SamsungSKU,
        SamsungProductDetailsType extends SamsungProductDetail<SamsungSKUType>,
        SamsungExceptionType extends SamsungException>
    extends BillingInventoryFetcher<
        SamsungSKUType,
        SamsungProductDetailsType,
        SamsungExceptionType>,
        OnGetItemListener
{
}
