package com.tradehero.common.billing.googleplay;

import com.tradehero.common.billing.BillingAvailableTesterHolder;
import com.tradehero.common.billing.googleplay.exception.IABException;


public interface IABBillingAvailableTesterHolder<IABExceptionType extends IABException>
    extends BillingAvailableTesterHolder<IABExceptionType>
{
}
