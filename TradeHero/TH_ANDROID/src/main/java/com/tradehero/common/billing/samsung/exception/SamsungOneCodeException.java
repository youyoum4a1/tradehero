package com.tradehero.common.billing.samsung.exception;


abstract public class SamsungOneCodeException extends SamsungException
{
    public final int errorCode;

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

    abstract protected int getOnlyValidErrorCode();
}
