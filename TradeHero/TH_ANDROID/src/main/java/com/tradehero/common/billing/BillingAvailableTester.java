package com.tradehero.common.billing;

import com.tradehero.common.billing.exception.BillingException;

public interface BillingAvailableTester<BillingExceptionType extends BillingException>
{
    int getRequestCode();
    OnBillingAvailableListener<BillingExceptionType> getBillingAvailableListener();
    void setBillingAvailableListener(OnBillingAvailableListener<BillingExceptionType> billingAvailableListener);
    void testBillingAvailable(int requestCode);

    public static interface OnBillingAvailableListener<BillingExceptionType extends BillingException>
    {
        void onBillingAvailable(int requestCode);
        void onBillingNotAvailable(int requestCode, BillingExceptionType billingException);
    }
}
