package com.tradehero.th.billing.googleplay.exception;

/** Created with IntelliJ IDEA. User: xavier Date: 11/18/13 Time: 12:52 PM To change this template use File | Settings | File Templates. */
public class UnhandledSKUDomainException extends RuntimeException
{
    public static final String TAG = UnhandledSKUDomainException.class.getSimpleName();

    public UnhandledSKUDomainException()
    {
        super();
    }

    public UnhandledSKUDomainException(String detailMessage)
    {
        super(detailMessage);
    }

    public UnhandledSKUDomainException(String detailMessage, Throwable throwable)
    {
        super(detailMessage, throwable);
    }

    public UnhandledSKUDomainException(Throwable throwable)
    {
        super(throwable);
    }
}
