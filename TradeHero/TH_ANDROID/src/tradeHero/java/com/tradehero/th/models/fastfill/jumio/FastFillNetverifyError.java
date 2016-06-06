package com.androidth.general.models.fastfill.jumio;

import android.support.annotation.NonNull;
import com.androidth.general.models.fastfill.FastFillException;

public class FastFillNetverifyError extends RuntimeException
    implements FastFillException
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

    @Override public boolean canRetry()
    {
        return errorCode.retryPossible;
    }
}
