package com.tradehero.common.billing.samsung;

import com.sec.android.iap.lib.listener.OnIapBindListener;
import com.tradehero.common.billing.BillingAvailableTester;
import com.tradehero.common.billing.samsung.exception.SamsungException;

/**
 * Created by xavier on 3/27/14.
 */
public interface SamsungBillingAvailableTester<SamsungExceptionType extends SamsungException>
    extends BillingAvailableTester<SamsungExceptionType>,
        OnIapBindListener
{
}
