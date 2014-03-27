package com.tradehero.common.billing.samsung.exception;

import com.tradehero.common.billing.samsung.SamsungConstants;

/** Created with IntelliJ IDEA. User: xavier Date: 11/5/13 Time: 4:15 PM To change this template use File | Settings | File Templates. */
public class SamsungIOException extends SamsungOneCodeException
{
    public static final int VALID_ERROR_CODE = SamsungConstants.IAP_ERROR_IOEXCEPTION_ERROR;

    //<editor-fold desc="Constructors">
    public SamsungIOException()
    {
        super(VALID_ERROR_CODE);
    }

    public SamsungIOException(String message)
    {
        super(VALID_ERROR_CODE, message);
    }

    public SamsungIOException(String message, Exception cause)
    {
        super(VALID_ERROR_CODE, message, cause);
    }

    public SamsungIOException(Exception cause)
    {
        super(VALID_ERROR_CODE, cause);
    }
    //</editor-fold>

    @Override protected int getOnlyValidErrorCode()
    {
        return VALID_ERROR_CODE;
    }
}
