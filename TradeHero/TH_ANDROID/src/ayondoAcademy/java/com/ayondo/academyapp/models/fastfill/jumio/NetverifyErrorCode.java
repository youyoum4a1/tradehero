package com.ayondo.academyapp.models.fastfill.jumio;

import android.support.annotation.NonNull;

public enum NetverifyErrorCode
{
    // We have encountered a network communication problem
    C100(100, true),
    C110(110, true),
    C120(120, true),
    C130(130, true),
    C140(140, true),
    C150(150, true),
    C160(160, true),

    // Authentication failed
    C200(200, false),
    C210(210, false),
    C220(220, false),

    // No Internet connection available
    C230(230, true),

    // Scanning not available this time, please contact the app vendor
    C240(240, false),

    // Cancelled by end-user
    C250(250, true),

    // The camera is currently not available
    C260(260, false),
    ;

    public final int value;
    public final boolean retryPossible;

    NetverifyErrorCode(int value, boolean retryPossible)
    {
        this.value = value;
        this.retryPossible = retryPossible;
    }

    @NonNull public static NetverifyErrorCode fromCode(int code)
    {
        for (NetverifyErrorCode candidate : values())
        {
            if (candidate.value == code)
            {
                return candidate;
            }
        }
        throw new IllegalArgumentException("Unhandled code: " + code);
    }
}
