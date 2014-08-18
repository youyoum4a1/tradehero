package com.tradehero.th.billing.samsung.exception;

import com.tradehero.common.billing.samsung.exception.SamsungOneCodeException;
import com.tradehero.th.billing.samsung.THSamsungConstants;

public class SamsungMissingCachedProductDetailException extends SamsungOneCodeException
{
    public static final int VALID_RESPONSE = THSamsungConstants.MISSING_CACHED_DETAIL;

    //<editor-fold desc="Constructors">
    public SamsungMissingCachedProductDetailException()
    {
        super(VALID_RESPONSE);
    }

    public SamsungMissingCachedProductDetailException(String message)
    {
        super(VALID_RESPONSE, message);
    }

    public SamsungMissingCachedProductDetailException(String message,
            Throwable cause)
    {
        super(VALID_RESPONSE, message, cause);
    }

    public SamsungMissingCachedProductDetailException(Throwable cause)
    {
        super(VALID_RESPONSE, cause);
    }
    //</editor-fold>

    @Override protected int getOnlyValidErrorCode()
    {
        return VALID_RESPONSE;
    }
}
