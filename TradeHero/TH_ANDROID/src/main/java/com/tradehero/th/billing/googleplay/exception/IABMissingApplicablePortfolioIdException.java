package com.tradehero.th.billing.googleplay.exception;

import com.tradehero.common.billing.googleplay.IABResult;
import com.tradehero.common.billing.googleplay.exception.IABOneResponseValueException;
import com.tradehero.th.billing.THBillingConstants;
import com.tradehero.th.billing.googleplay.THIABConstants;

public class IABMissingApplicablePortfolioIdException extends IABOneResponseValueException
{
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
