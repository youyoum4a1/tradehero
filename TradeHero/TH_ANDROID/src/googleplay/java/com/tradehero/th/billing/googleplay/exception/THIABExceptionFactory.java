package com.ayondo.academy.billing.googleplay.exception;

import com.tradehero.common.billing.googleplay.exception.IABException;
import com.tradehero.common.billing.googleplay.exception.IABExceptionFactory;
import javax.inject.Inject;

public class THIABExceptionFactory extends IABExceptionFactory
{
    //<editor-fold desc="Constructors">
    @Inject public THIABExceptionFactory()
    {
        super();
    }
    //</editor-fold>

    @Override public IABException create(int responseStatus, String message)
    {
        IABException exception = super.create(responseStatus, message);
        if (exception == null)
        {
            switch (responseStatus)
            {
                case THIABExceptionConstants.UNHANDLED_DOMAIN: // -2000
                    exception = new IABUnhandledSKUDomainException(message);
                    break;

                case THIABExceptionConstants.PURCHASE_REPORT_RETROFIT_ERROR: // -2001
                    exception = new IABPurchaseReportRetrofitException(message);
                    break;

                case THIABExceptionConstants.MISSING_CACHED_DETAIL: // -2002
                    exception = new IABMissingCachedProductDetailException(message);
                    break;

                case THIABExceptionConstants.MISSING_APPLICABLE_PORTFOLIO_ID: // -2003
                    exception = new IABMissingApplicablePortfolioIdException(message);
                    break;
            }
        }
        return exception;
    }
}
