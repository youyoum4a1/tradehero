package com.tradehero.common.billing.samsung;

import com.sec.android.iap.lib.listener.OnIapBindListener;
import com.tradehero.common.billing.BillingAvailableTester;
import com.tradehero.common.billing.samsung.exception.SamsungException;

public interface SamsungBillingAvailableTester<SamsungExceptionType extends SamsungException>
    extends BillingAvailableTester<SamsungExceptionType>,
        OnIapBindListener
{
}
