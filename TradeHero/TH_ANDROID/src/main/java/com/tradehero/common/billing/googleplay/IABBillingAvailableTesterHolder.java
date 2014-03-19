package com.tradehero.common.billing.googleplay;

import com.tradehero.common.billing.BillingAvailableTesterHolder;
import com.tradehero.common.billing.googleplay.exception.IABException;

/** Created with IntelliJ IDEA. User: xavier Date: 11/8/13 Time: 11:06 AM To change this template use File | Settings | File Templates. */
public interface IABBillingAvailableTesterHolder<IABExceptionType extends IABException>
    extends BillingAvailableTesterHolder<IABExceptionType>
{
}
