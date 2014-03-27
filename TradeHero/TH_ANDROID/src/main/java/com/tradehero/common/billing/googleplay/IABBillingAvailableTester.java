package com.tradehero.common.billing.googleplay;

import com.tradehero.common.billing.BillingAvailableTester;
import com.tradehero.common.billing.googleplay.exception.IABException;

/**
 * Created by xavier on 3/27/14.
 */
public interface IABBillingAvailableTester<IABExceptionType extends IABException>
    extends BillingAvailableTester<IABExceptionType>
{
}
