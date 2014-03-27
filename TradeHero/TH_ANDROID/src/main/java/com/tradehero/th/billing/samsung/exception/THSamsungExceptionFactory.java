package com.tradehero.th.billing.samsung.exception;

import com.sec.android.iap.lib.vo.ErrorVo;
import com.tradehero.common.billing.samsung.exception.SamsungException;
import com.tradehero.common.billing.samsung.exception.SamsungExceptionFactory;
import com.tradehero.th.billing.samsung.THSamsungConstants;
import javax.inject.Inject;

/**
 * Created by xavier on 3/21/14.
 */
public class THSamsungExceptionFactory extends SamsungExceptionFactory
{
    @Inject public THSamsungExceptionFactory()
    {
        super();
    }

    @Override public SamsungException create(int responseStatus, String message)
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
            }
        }
        return exception;
    }
}
