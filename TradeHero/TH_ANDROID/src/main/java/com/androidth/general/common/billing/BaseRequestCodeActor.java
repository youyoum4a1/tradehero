package com.androidth.general.common.billing;

public class BaseRequestCodeActor implements RequestCodeActor
{
    private final int requestCode;

    //<editor-fold desc="Constructors">
    public BaseRequestCodeActor(int requestCode)
    {
        this.requestCode = requestCode;
    }
    //</editor-fold>

    @Override public int getRequestCode()
    {
        return requestCode;
    }
}
