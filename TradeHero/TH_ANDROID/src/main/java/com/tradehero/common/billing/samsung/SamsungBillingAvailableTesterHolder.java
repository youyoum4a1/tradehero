package com.tradehero.common.billing.samsung;

import com.tradehero.common.billing.BillingAvailableTesterHolder;
import com.tradehero.common.billing.exception.BillingException;

/**
 * Created by xavier on 3/27/14.
 */
public interface SamsungBillingAvailableTesterHolder<BillingExceptionType extends BillingException>
    extends BillingAvailableTesterHolder<BillingExceptionType>
{
}
