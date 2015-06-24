package com.tradehero.th.models.jumio;

import android.support.annotation.NonNull;

public class FastFillNetverifyError extends RuntimeException
{
    @NonNull public final String scanReference;
    @NonNull public final NetverifyErrorCode errorCode;

    public FastFillNetverifyError(@NonNull String errorMessage, @NonNull String scanReference, int errorCode)
    {
        this(errorMessage, scanReference, NetverifyErrorCode.fromCode(errorCode));
    }

    public FastFillNetverifyError(@NonNull String errorMessage, @NonNull String scanReference, @NonNull NetverifyErrorCode errorCode)
    {
        super(errorMessage);
        this.scanReference = scanReference;
        this.errorCode = errorCode;
    }
}
