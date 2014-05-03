package com.tradehero.common.billing.samsung.exception;

import com.tradehero.common.billing.exception.BillingException;


abstract public class SamsungException extends BillingException
{
    public SamsungException()
    {
        super();
    }

    public SamsungException(String message)
    {
        super(message);
    }

    public SamsungException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public SamsungException(Throwable cause)
    {
        super(cause);
    }
}
