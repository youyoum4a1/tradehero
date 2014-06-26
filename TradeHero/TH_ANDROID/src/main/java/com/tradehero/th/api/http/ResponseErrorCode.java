package com.tradehero.th.api.http;

import org.jetbrains.annotations.Nullable;

public enum ResponseErrorCode
{
    OutDatedVersion(0),
    ExpiredSocialToken(1),
    ;

    public final int code;

    ResponseErrorCode(int code)
    {
        this.code = code;
    }

    @Nullable public static ResponseErrorCode getByCode(int code)
    {
        for (ResponseErrorCode responseErrorCode: values())
        {
            if (responseErrorCode.code == code)
            {
                return responseErrorCode;
            }
        }
        return null;
    }
}
