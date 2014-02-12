package com.tradehero.th.billing.googleplay.exception;

/**
 * Created by xavier on 2/12/14.
 */
public class PurchaseReportedToOtherUserException extends RuntimeException
{
    public static final String TAG = PurchaseReportedToOtherUserException.class.getSimpleName();

    public PurchaseReportedToOtherUserException()
    {
        super();
    }

    public PurchaseReportedToOtherUserException(String detailMessage)
    {
        super(detailMessage);
    }

    public PurchaseReportedToOtherUserException(String detailMessage, Throwable throwable)
    {
        super(detailMessage, throwable);
    }

    public PurchaseReportedToOtherUserException(Throwable throwable)
    {
        super(throwable);
    }

    protected PurchaseReportedToOtherUserException(String s, Throwable throwable, boolean b, boolean b2)
    {
        super(s, throwable, b, b2);
    }
}
