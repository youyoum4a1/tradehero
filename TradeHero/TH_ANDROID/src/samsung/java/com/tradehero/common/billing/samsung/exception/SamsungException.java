package com.androidth.general.common.billing.samsung.exception;

import com.androidth.general.common.billing.exception.BillingException;

abstract public class SamsungException extends BillingException
{
    //<editor-fold desc="Constructors">
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
    //</editor-fold>
}
