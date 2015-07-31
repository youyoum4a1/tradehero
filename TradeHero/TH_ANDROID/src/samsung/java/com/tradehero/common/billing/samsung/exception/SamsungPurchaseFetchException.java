package com.tradehero.common.billing.samsung.exception;

import android.support.annotation.NonNull;
import com.samsung.android.sdk.iap.lib.vo.ErrorVo;

public class SamsungPurchaseFetchException extends SamsungVoException
{
    @NonNull public final String groupId;

    //<editor-fold desc="Constructors">
    public SamsungPurchaseFetchException(@NonNull ErrorVo errorVo, @NonNull String groupId)
    {
        super(errorVo);
        this.groupId = groupId;
    }

    public SamsungPurchaseFetchException(String message, @NonNull ErrorVo errorVo, @NonNull String groupId)
    {
        super(message, errorVo);
        this.groupId = groupId;
    }
    //</editor-fold>
}
