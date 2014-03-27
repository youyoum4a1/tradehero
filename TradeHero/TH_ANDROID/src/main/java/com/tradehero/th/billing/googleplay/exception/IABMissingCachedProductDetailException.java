package com.tradehero.th.billing.googleplay.exception;

import com.tradehero.common.billing.googleplay.IABResult;
import com.tradehero.common.billing.googleplay.exception.IABOneResponseValueException;
import com.tradehero.th.billing.googleplay.THIABConstants;

/** Created with IntelliJ IDEA. User: xavier Date: 11/18/13 Time: 12:52 PM To change this template use File | Settings | File Templates. */
public class IABMissingCachedProductDetailException extends IABOneResponseValueException
{
    public static final String TAG = IABMissingCachedProductDetailException.class.getSimpleName();
    public static final int VALID_RESPONSE = THIABConstants.MISSING_CACHED_DETAIL;

    public IABMissingCachedProductDetailException(IABResult r)
    {
        super(r);
    }

    public IABMissingCachedProductDetailException(IABResult r, Exception cause)
    {
        super(r, cause);
    }

    public IABMissingCachedProductDetailException(String message)
    {
        super(VALID_RESPONSE, message);
    }

    public IABMissingCachedProductDetailException(String message, Exception cause)
    {
        super(VALID_RESPONSE, message, cause);
    }

    @Override protected int getOnlyValidResponse()
    {
        return VALID_RESPONSE;
    }
}
