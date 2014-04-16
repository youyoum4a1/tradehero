package com.tradehero.common.billing;

import com.tradehero.common.billing.exception.BillingException;

/** Created with IntelliJ IDEA. User: xavier Date: 11/27/13 Time: 1:23 PM To change this template use File | Settings | File Templates. */
public interface BillingAvailableTesterHolder<BillingExceptionType extends BillingException>
{
    boolean isUnusedRequestCode(int requestCode);
    void forgetRequestCode(int requestCode);
    BillingAvailableTester.OnBillingAvailableListener<BillingExceptionType> getBillingAvailableListener(int requestCode);
    void registerBillingAvailableListener(int requestCode, BillingAvailableTester.OnBillingAvailableListener<BillingExceptionType> billingAvailableListener);
    void launchBillingAvailableTestSequence(int requestCode);
    void onDestroy();
}
