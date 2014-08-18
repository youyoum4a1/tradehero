package com.tradehero.common.billing;

import com.tradehero.common.billing.exception.BillingException;

public interface BillingAvailableTesterHolder<BillingExceptionType extends BillingException>
    extends RequestCodeHolder
{
    BillingAvailableTester.OnBillingAvailableListener<BillingExceptionType> getBillingAvailableListener(int requestCode);
    void registerBillingAvailableListener(int requestCode, BillingAvailableTester.OnBillingAvailableListener<BillingExceptionType> billingAvailableListener);
    void launchBillingAvailableTestSequence(int requestCode);
}
