package com.tradehero.th.billing.googleplay.exception;

import com.tradehero.common.billing.googleplay.IABResult;
import com.tradehero.common.billing.googleplay.exception.IABOneResponseValueException;
import com.tradehero.th.billing.googleplay.THIABConstants;

public class MissingApplicablePortfolioIdException extends IABOneResponseValueException
{
    public static final int VALID_RESPONSE = THIABConstants.MISSING_APPLICABLE_PORTFOLIO_ID;

    public MissingApplicablePortfolioIdException(IABResult r)
    {
        super(r);
    }

    public MissingApplicablePortfolioIdException(IABResult r, Exception cause)
    {
        super(r, cause);
    }

    public MissingApplicablePortfolioIdException(String message)
    {
        super(VALID_RESPONSE, message);
    }

    public MissingApplicablePortfolioIdException(String message, Exception cause)
    {
        super(VALID_RESPONSE, message, cause);
    }

    @Override protected int getOnlyValidResponse()
    {
        return VALID_RESPONSE;
    }
}
