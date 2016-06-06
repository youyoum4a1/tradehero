package com.androidth.general.common.billing.samsung.exception;

import android.support.annotation.NonNull;
import com.samsung.android.sdk.iap.lib.vo.ErrorVo;

public class SamsungItemListException extends SamsungVoException
{
    public final int mode;

    //<editor-fold desc="Constructors">
    public SamsungItemListException(@NonNull ErrorVo errorVo, int mode)
    {
        super(errorVo);
        this.mode = mode;
    }

    public SamsungItemListException(String message, @NonNull ErrorVo errorVo, int mode)
    {
        super(message, errorVo);
        this.mode = mode;
    }
    //</editor-fold>
}
