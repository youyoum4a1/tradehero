package com.tradehero.common.billing.googleplay.exception;

import com.tradehero.common.billing.googleplay.IABResult;


abstract public class IABOneResponseValueException extends IABException
{
    public static final String TAG = IABOneResponseValueException.class.getSimpleName();

    public IABOneResponseValueException(IABResult r)
    {
        super(r);
        validate();
    }

    public IABOneResponseValueException(IABResult r, Exception cause)
    {
        super(r, cause);
        validate();
    }

    protected IABOneResponseValueException(int response, String message)
    {
        super(response, message);
    }

    protected IABOneResponseValueException(int response, String message, Exception cause)
    {
        super(response, message, cause);
    }

    public IABOneResponseValueException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public IABOneResponseValueException(Throwable cause)
    {
        super(cause);
    }

    abstract protected int getOnlyValidResponse();

    protected void validate()
    {
        if (getResult().response != getOnlyValidResponse())
        {
            throw new IllegalArgumentException("Cannot handle response other than " + getOnlyValidResponse());
        }
    }
}
