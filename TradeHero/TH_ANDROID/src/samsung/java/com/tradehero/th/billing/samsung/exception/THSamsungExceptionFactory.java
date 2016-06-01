package com.ayondo.academy.billing.samsung.exception;

import android.support.annotation.Nullable;
import com.tradehero.common.billing.samsung.exception.SamsungException;
import com.tradehero.common.billing.samsung.exception.SamsungExceptionFactory;
import com.ayondo.academy.billing.samsung.THSamsungConstants;
import javax.inject.Inject;

public class THSamsungExceptionFactory extends SamsungExceptionFactory
{
    //<editor-fold desc="Constrcutors">
    @Inject public THSamsungExceptionFactory()
    {
        super();
    }
    //</editor-fold>

    @Override @Nullable public SamsungException create(int responseStatus, String message)
    {
        SamsungException exception = super.create(responseStatus, message);
        if (exception == null)
        {
            switch (responseStatus)
            {
                case THSamsungConstants.UNHANDLED_DOMAIN: // -2000
                    exception = new SamsungUnhandledSKUDomainException(message);
                    break;

                case THSamsungConstants.PURCHASE_REPORT_RETROFIT_ERROR: // -2001
                    exception = new SamsungPurchaseReportRetrofitException(message);
                    break;

                case THSamsungConstants.MISSING_CACHED_DETAIL: // -2002
                    exception = new SamsungMissingCachedProductDetailException(message);
                    break;

                case THSamsungConstants.MISSING_APPLICABLE_PORTFOLIO_ID: // -2003
                    exception = new SamsungMissingApplicablePortfolioIdException(message);
                    break;

                case THSamsungConstants.INVALID_QUANTITY: // -2004
                    exception = new SamsungInvalidQuantityException(message);
                    break;
            }
        }
        return exception;
    }
}
