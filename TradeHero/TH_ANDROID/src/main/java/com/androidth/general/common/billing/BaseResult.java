package com.androidth.general.common.billing;

abstract public class BaseResult
{
    public final int requestCode;

    //<editor-fold desc="Constructors">
    public BaseResult(int requestCode)
    {
        this.requestCode = requestCode;
    }
    //</editor-fold>
}
