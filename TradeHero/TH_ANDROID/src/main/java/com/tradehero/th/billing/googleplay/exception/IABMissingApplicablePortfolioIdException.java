package com.tradehero.th.billing.googleplay.exception;

import com.tradehero.common.billing.googleplay.IABResult;
import com.tradehero.common.billing.googleplay.exception.IABOneResponseValueException;

public class IABMissingApplicablePortfolioIdException extends IABOneResponseValueException
{
    public static final int VALID_RESPONSE = THIABExceptionConstants.MISSING_APPLICABLE_PORTFOLIO_ID;

    //<editor-fold desc="Constructors">
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
    //</editor-fold>

    @Override protected int getOnlyValidResponse()
    {
        return VALID_RESPONSE;
    }
}
