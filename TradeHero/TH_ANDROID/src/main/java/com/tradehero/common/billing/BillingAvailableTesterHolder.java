package com.tradehero.common.billing;

import com.tradehero.common.billing.exception.BillingException;

import android.support.annotation.Nullable;

public interface BillingAvailableTesterHolder<BillingExceptionType extends BillingException>
    extends RequestCodeHolder
{
    @Nullable BillingAvailableTester.OnBillingAvailableListener<BillingExceptionType> getBillingAvailableListener(int requestCode);
    void registerBillingAvailableListener(int requestCode, @Nullable BillingAvailableTester.OnBillingAvailableListener<BillingExceptionType> billingAvailableListener);
    void launchBillingAvailableTestSequence(int requestCode);
}
