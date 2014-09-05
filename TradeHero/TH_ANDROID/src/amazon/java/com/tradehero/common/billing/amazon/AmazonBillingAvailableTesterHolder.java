package com.tradehero.common.billing.amazon;

import com.tradehero.common.billing.BillingAvailableTesterHolder;
import com.tradehero.common.billing.exception.BillingException;

public interface AmazonBillingAvailableTesterHolder<BillingExceptionType extends BillingException>
    extends BillingAvailableTesterHolder<BillingExceptionType>
{
}
