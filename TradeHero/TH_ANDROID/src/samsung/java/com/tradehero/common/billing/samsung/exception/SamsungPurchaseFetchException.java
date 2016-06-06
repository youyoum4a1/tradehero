package com.androidth.general.common.billing.samsung.exception;

import android.support.annotation.NonNull;
import com.samsung.android.sdk.iap.lib.vo.ErrorVo;

public class SamsungPurchaseFetchException extends SamsungVoException
{
    //<editor-fold desc="Constructors">
    public SamsungPurchaseFetchException(@NonNull ErrorVo errorVo)
    {
        super(errorVo);
    }

    public SamsungPurchaseFetchException(String message, @NonNull ErrorVo errorVo)
    {
        super(message, errorVo);
    }
    //</editor-fold>
}
