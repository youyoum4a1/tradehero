package com.tradehero.common.billing;

import com.tradehero.common.billing.exception.BillingException;

public interface BillingAvailableTesterHolder<BillingExceptionType extends BillingException>
{
    boolean isUnusedRequestCode(int requestCode);
    void forgetRequestCode(int requestCode);
    BillingAvailableTester.OnBillingAvailableListener<BillingExceptionType> getBillingAvailableListener(int requestCode);
    void registerBillingAvailableListener(int requestCode, BillingAvailableTester.OnBillingAvailableListener<BillingExceptionType> billingAvailableListener);
    void launchBillingAvailableTestSequence(int requestCode);
    void onDestroy();
}
