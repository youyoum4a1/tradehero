package com.tradehero.common.billing.amazon.exception;

import com.tradehero.common.billing.exception.BillingException;

public class AmazonException extends BillingException
{
    public AmazonException(String message)
    {
        super(message);
    }

    public AmazonException(Throwable cause)
    {
        super(cause);
    }
}