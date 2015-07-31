package com.tradehero.common.billing.samsung.exception;

import android.support.annotation.NonNull;
import com.samsung.android.sdk.iap.lib.vo.ErrorVo;

public class SamsungItemListException extends SamsungVoException
{
    @NonNull public final String groupId;
    public final int mode;

    //<editor-fold desc="Constructors">
    public SamsungItemListException(@NonNull ErrorVo errorVo, @NonNull String groupId, int mode)
    {
        super(errorVo);
        this.groupId = groupId;
        this.mode = mode;
    }

    public SamsungItemListException(String message, @NonNull ErrorVo errorVo, @NonNull String groupId, int mode)
    {
        super(message, errorVo);
        this.groupId = groupId;
        this.mode = mode;
    }
    //</editor-fold>
}
