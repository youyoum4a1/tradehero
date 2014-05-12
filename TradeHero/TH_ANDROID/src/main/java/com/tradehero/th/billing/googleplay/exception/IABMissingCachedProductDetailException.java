package com.tradehero.th.billing.googleplay.exception;

import com.tradehero.common.billing.googleplay.IABResult;
import com.tradehero.common.billing.googleplay.exception.IABOneResponseValueException;
import com.tradehero.th.billing.googleplay.THIABConstants;

public class IABMissingCachedProductDetailException extends IABOneResponseValueException
{
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
