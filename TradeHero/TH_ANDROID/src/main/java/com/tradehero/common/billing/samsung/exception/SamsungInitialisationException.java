package com.tradehero.common.billing.samsung.exception;

import com.sec.android.iap.lib.helper.SamsungIapHelper;

/** Created with IntelliJ IDEA. User: xavier Date: 11/5/13 Time: 4:15 PM To change this template use File | Settings | File Templates. */
public class SamsungInitialisationException extends SamsungOneCodeException
{
    public static final int VALID_ERROR_CODE = SamsungIapHelper.IAP_ERROR_INITIALIZATION;

    //<editor-fold desc="Constructors">
    public SamsungInitialisationException()
    {
        super(VALID_ERROR_CODE);
    }

    public SamsungInitialisationException(String message)
    {
        super(VALID_ERROR_CODE, message);
    }

    public SamsungInitialisationException(String message, Exception cause)
    {
        super(VALID_ERROR_CODE, message, cause);
    }

    public SamsungInitialisationException(Exception cause)
    {
        super(VALID_ERROR_CODE, cause);
    }
    //</editor-fold>

    @Override protected int getOnlyValidErrorCode()
    {
        return VALID_ERROR_CODE;
    }
}
