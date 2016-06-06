package com.ayondo.academyapp.models.fastfill;

import android.support.annotation.NonNull;

public class FastFillExceptionUtil
{
    public static boolean canRetry(@NonNull Throwable throwable)
    {
        return throwable instanceof FastFillException && ((FastFillException) throwable).canRetry();
    }
}
