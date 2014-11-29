package com.tradehero.common.billing;

import android.support.annotation.Nullable;
import com.tradehero.common.billing.exception.BillingException;

public interface BillingAvailableTesterHolder<BillingExceptionType extends BillingException>
    extends RequestCodeHolder
{
    @Nullable BillingAvailableTester.OnBillingAvailableListener<BillingExceptionType> getBillingAvailableListener(int requestCode);
    void registerBillingAvailableListener(int requestCode, @Nullable BillingAvailableTester.OnBillingAvailableListener<BillingExceptionType> billingAvailableListener);
    void launchBillingAvailableTestSequence(int requestCode);
}
