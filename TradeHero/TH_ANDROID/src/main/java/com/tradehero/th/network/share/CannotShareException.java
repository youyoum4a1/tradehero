package com.tradehero.th.network.share;

public class CannotShareException extends RuntimeException
{
    //<editor-fold desc="Constructors">
    public CannotShareException()
    {
        super();
    }

    public CannotShareException(String message)
    {
        super(message);
    }

    public CannotShareException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public CannotShareException(Throwable cause)
    {
        super(cause);
    }

    protected CannotShareException(String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace)
    {
        super(message, cause);
        //super(message, cause, enableSuppression, writableStackTrace);
    }
    //</editor-fold>
}
