package com.tradehero.common.billing.googleplay;

/** Created with IntelliJ IDEA. User: xavier Date: 11/5/13 Time: 4:20 PM To change this template use File | Settings | File Templates. */
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

    abstract protected int getOnlyValidResponse();

    protected void validate()
    {
        if (getResult().response != getOnlyValidResponse())
        {
            throw new IllegalArgumentException("Cannot handle response other than " + getOnlyValidResponse());
        }
    }
}
