package com.androidth.general.billing.samsung.exception;

import com.androidth.general.common.billing.samsung.exception.SamsungOneCodeException;
import com.androidth.general.billing.samsung.THSamsungConstants;

public class SamsungMissingCachedProductDetailException extends SamsungOneCodeException
{
    public static final int VALID_RESPONSE = THSamsungConstants.MISSING_CACHED_DETAIL;

    //<editor-fold desc="Constructors">
    public SamsungMissingCachedProductDetailException(String message)
    {
        super(VALID_RESPONSE, message);
    }
    //</editor-fold>

    @Override protected int getOnlyValidErrorCode()
    {
        return VALID_RESPONSE;
    }
}
