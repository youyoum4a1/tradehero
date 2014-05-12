package com.tradehero.common.billing.googleplay;

import com.tradehero.common.billing.BillingAvailableTester;
import com.tradehero.common.billing.googleplay.exception.IABException;

public interface IABBillingAvailableTester<IABExceptionType extends IABException>
    extends BillingAvailableTester<IABExceptionType>
{
}
