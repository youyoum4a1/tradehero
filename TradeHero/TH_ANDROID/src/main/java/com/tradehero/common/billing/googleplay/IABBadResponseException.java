package com.tradehero.common.billing.googleplay;

/** Created with IntelliJ IDEA. User: xavier Date: 11/5/13 Time: 4:15 PM To change this template use File | Settings | File Templates. */
public class IABBadResponseException extends IABOneResponseValueException
{
    public static final String TAG = IABBadResponseException.class.getSimpleName();
    public static final int VALID_RESPONSE = Constants.IABHELPER_BAD_RESPONSE;

    public IABBadResponseException(IABResult r)
    {
        super(r);
    }

    public IABBadResponseException(IABResult r, Exception cause)
    {
        super(r, cause);
    }

    public IABBadResponseException(String message)
    {
        super(VALID_RESPONSE, message);
    }

    public IABBadResponseException(String message, Exception cause)
    {
        super(VALID_RESPONSE, message, cause);
    }

    @Override protected int getOnlyValidResponse()
    {
        return VALID_RESPONSE;
    }
}
