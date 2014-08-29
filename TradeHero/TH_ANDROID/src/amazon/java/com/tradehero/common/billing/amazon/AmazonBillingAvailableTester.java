package com.tradehero.common.billing.amazon;

import com.tradehero.common.billing.amazon.exception.AmazonException;
import com.tradehero.common.billing.BillingAvailableTester;

public interface AmazonBillingAvailableTester<AmazonExceptionType extends AmazonException>
    extends BillingAvailableTester<AmazonExceptionType>
{
}
