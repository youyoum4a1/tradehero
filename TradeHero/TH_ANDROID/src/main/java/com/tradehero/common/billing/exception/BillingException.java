package com.tradehero.common.billing.exception;

/**
 * Created by xavier on 2/21/14.
 */
public class BillingException extends Exception
{
    //<editor-fold desc="Constructors">
    public BillingException()
    {
        super();
    }

    public BillingException(String message)
    {
        super(message);
    }

    public BillingException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public BillingException(Throwable cause)
    {
        super(cause);
    }
    //</editor-fold>
}
