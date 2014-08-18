package com.tradehero.th.billing.samsung.exception;

import com.tradehero.common.billing.samsung.exception.SamsungOneCodeException;
import com.tradehero.th.billing.samsung.THSamsungConstants;

public class SamsungMissingApplicablePortfolioIdException extends SamsungOneCodeException
{
    public static final int VALID_RESPONSE = THSamsungConstants.MISSING_APPLICABLE_PORTFOLIO_ID;

    //<editor-fold desc="Constructors">
    public SamsungMissingApplicablePortfolioIdException()
    {
        super(VALID_RESPONSE);
    }

    public SamsungMissingApplicablePortfolioIdException(String message)
    {
        super(VALID_RESPONSE, message);
    }

    public SamsungMissingApplicablePortfolioIdException(String message,
            Throwable cause)
    {
        super(VALID_RESPONSE, message, cause);
    }

    public SamsungMissingApplicablePortfolioIdException(Throwable cause)
    {
        super(VALID_RESPONSE, cause);
    }
    //</editor-fold>

    @Override protected int getOnlyValidErrorCode()
    {
        return VALID_RESPONSE;
    }
}
