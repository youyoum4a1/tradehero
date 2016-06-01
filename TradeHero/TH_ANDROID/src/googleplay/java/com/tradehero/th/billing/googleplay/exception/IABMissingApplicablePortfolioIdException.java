package com.ayondo.academy.billing.googleplay.exception;

import com.tradehero.common.billing.googleplay.exception.IABOneResponseValueException;

public class IABMissingApplicablePortfolioIdException extends IABOneResponseValueException
{
    public static final int VALID_RESPONSE = THIABExceptionConstants.MISSING_APPLICABLE_PORTFOLIO_ID;

    //<editor-fold desc="Constructors">
    public IABMissingApplicablePortfolioIdException(String message)
    {
        super(VALID_RESPONSE, message);
    }
    //</editor-fold>

    @Override protected int getOnlyValidResponse()
    {
        return VALID_RESPONSE;
    }
}
