package com.tradehero.common.billing.samsung.exception;

import android.support.annotation.NonNull;
import com.samsung.android.sdk.iap.lib.vo.ErrorVo;
import com.tradehero.common.billing.exception.BillingException;

public class SamsungVoException extends BillingException
{
    @NonNull public final ErrorVo errorVo;

    //<editor-fold desc="Constructors">
    public SamsungVoException(@NonNull ErrorVo errorVo)
    {
        this.errorVo = errorVo;
    }

    public SamsungVoException(String message, @NonNull ErrorVo errorVo)
    {
        super(message);
        this.errorVo = errorVo;
    }
    //</editor-fold>
}
