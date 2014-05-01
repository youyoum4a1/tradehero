package com.tradehero.th.billing.googleplay.exception;

public class PurchaseReportedToOtherUserException extends RuntimeException
{
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
}
