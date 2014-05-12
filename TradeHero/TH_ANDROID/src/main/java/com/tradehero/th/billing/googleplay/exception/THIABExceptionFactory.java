package com.tradehero.th.billing.googleplay.exception;

import com.tradehero.common.billing.googleplay.exception.IABException;
import com.tradehero.common.billing.googleplay.exception.IABExceptionFactory;
import com.tradehero.th.billing.googleplay.THIABConstants;
import javax.inject.Inject;

public class THIABExceptionFactory extends IABExceptionFactory
{
    @Inject public THIABExceptionFactory()
    {
        super();
    }

    @Override public IABException create(int responseStatus, String message)
    {
        IABException exception = super.create(responseStatus, message);
        if (exception == null)
        {
            switch (responseStatus)
            {
                case THIABConstants.UNHANDLED_DOMAIN: // -2000
                    exception = new IABUnhandledSKUDomainException(message);
                    break;

                case THIABConstants.PURCHASE_REPORT_RETROFIT_ERROR: // -2001
                    exception = new IABPurchaseReportRetrofitException(message);
                    break;

                case THIABConstants.MISSING_CACHED_DETAIL: // -2002
                    exception = new IABMissingCachedProductDetailException(message);
                    break;

                case THIABConstants.MISSING_APPLICABLE_PORTFOLIO_ID: // -2003
                    exception = new IABMissingApplicablePortfolioIdException(message);
                    break;
            }
        }
        return exception;
    }
}
