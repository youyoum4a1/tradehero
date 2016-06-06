package com.androidth.general.billing.exception;

public class PurchaseReportedToOtherUserException extends RuntimeException
{
    //<editor-fold desc="Constructors">
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
    //</editor-fold>
}
