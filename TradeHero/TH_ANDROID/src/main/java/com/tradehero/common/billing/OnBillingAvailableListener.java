package com.tradehero.common.billing;

import com.tradehero.common.billing.exception.BillingException;

/**
 * Created by xavier on 2/24/14.
 */
public interface OnBillingAvailableListener<
        BillingExceptionType extends BillingException>
{
    void onBillingAvailable(int requestCode);
    void onBillingNotAvailable(int requestCode, BillingExceptionType billingException);
}
