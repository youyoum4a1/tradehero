package com.tradehero.common.billing.samsung.exception;

import android.support.annotation.NonNull;
import com.samsung.android.sdk.iap.lib.vo.ErrorVo;

public class SamsungPurchaseException extends SamsungVoException
{
    @NonNull public final String itemId;
    public final boolean showSuccessDialog;

    //<editor-fold desc="Constructors">
    public SamsungPurchaseException(
            @NonNull ErrorVo errorVo,
            @NonNull String itemId,
            boolean showSuccessDialog)
    {
        super(errorVo);
        this.itemId = itemId;
        this.showSuccessDialog = showSuccessDialog;
    }

    public SamsungPurchaseException(
            String message,
            @NonNull ErrorVo errorVo,
            @NonNull String itemId,
            boolean showSuccessDialog)
    {
        super(message, errorVo);
        this.itemId = itemId;
        this.showSuccessDialog = showSuccessDialog;
    }
    //</editor-fold>
}
