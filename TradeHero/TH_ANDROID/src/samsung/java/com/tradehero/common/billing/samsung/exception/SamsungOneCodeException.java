package com.androidth.general.common.billing.samsung.exception;

abstract public class SamsungOneCodeException extends SamsungException
{
    public final int errorCode;

    //<editor-fold desc="Constructors">
    public SamsungOneCodeException(int errorCode)
    {
        super();
        this.errorCode = errorCode;
    }

    public SamsungOneCodeException(int errorCode, String message)
    {
        super(message);
        this.errorCode = errorCode;
    }

    public SamsungOneCodeException(int errorCode, String message, Throwable cause)
    {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public SamsungOneCodeException(int errorCode, Throwable cause)
    {
        super(cause);
        this.errorCode = errorCode;
    }
    //</editor-fold>

    abstract protected int getOnlyValidErrorCode();
}
