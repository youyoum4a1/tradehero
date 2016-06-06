package com.androidth.general.common.billing.samsung.exception;

public class SamsungBindException extends SamsungException
{
    public final int result;

    //<editor-fold desc="Constructors">
    public SamsungBindException(String message, int result)
    {
        super(message);
        this.result = result;
    }

    public SamsungBindException(int result)
    {
        this.result = result;
    }
    //</editor-fold>
}
