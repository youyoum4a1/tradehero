package com.tradehero.th.billing.googleplay.exception;

import com.tradehero.common.billing.googleplay.IABResult;
import com.tradehero.common.billing.googleplay.exception.IABOneResponseValueException;
import com.tradehero.th.billing.THBillingConstants;
import com.tradehero.th.billing.googleplay.THIABConstants;

/** Created with IntelliJ IDEA. User: xavier Date: 11/18/13 Time: 12:52 PM To change this template use File | Settings | File Templates. */
public class IABMissingApplicablePortfolioIdException extends IABOneResponseValueException
{
    public static final String TAG = IABMissingApplicablePortfolioIdException.class.getSimpleName();
    public static final int VALID_RESPONSE = THIABConstants.MISSING_APPLICABLE_PORTFOLIO_ID;

    public IABMissingApplicablePortfolioIdException(IABResult r)
    {
        super(r);
    }

    public IABMissingApplicablePortfolioIdException(IABResult r, Exception cause)
    {
        super(r, cause);
    }

    public IABMissingApplicablePortfolioIdException(String message)
    {
        super(VALID_RESPONSE, message);
    }

    public IABMissingApplicablePortfolioIdException(String message, Exception cause)
    {
        super(VALID_RESPONSE, message, cause);
    }

    @Override protected int getOnlyValidResponse()
    {
        return VALID_RESPONSE;
    }
}
