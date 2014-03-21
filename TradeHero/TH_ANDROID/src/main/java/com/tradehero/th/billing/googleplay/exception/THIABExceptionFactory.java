package com.tradehero.th.billing.googleplay.exception;

import com.tradehero.common.billing.googleplay.exception.IABException;
import com.tradehero.common.billing.googleplay.exception.IABExceptionFactory;
import com.tradehero.th.billing.googleplay.THIABConstants;
import javax.inject.Inject;

/**
 * Created by xavier on 3/21/14.
 */
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
                    exception = new UnhandledSKUDomainException(message);
                    break;

                case THIABConstants.PURCHASE_REPORT_RETROFIT_ERROR: // -2001
                    exception = new PurchaseReportRetrofitException(message);
                    break;

                case THIABConstants.MISSING_CACHED_DETAIL: // -2000
                    exception = new MissingCachedProductDetailException(message);
                    break;
            }
        }
        return exception;
    }
}
