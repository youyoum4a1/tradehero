package com.ayondo.academy.api.http;

import android.support.annotation.Nullable;
import timber.log.Timber;

public enum ResponseErrorCode
{
    OutDatedVersion,
    ExpiredSocialToken;

    @Nullable public static ResponseErrorCode getByCode(String code)
    {
        try
        {
            return ResponseErrorCode.valueOf(code);
        } catch (IllegalArgumentException e)
        {
            Timber.e("Invalid error code %s", code);
            return null;
        }
    }
}
