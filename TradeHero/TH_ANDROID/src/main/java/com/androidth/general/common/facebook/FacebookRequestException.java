package com.androidth.general.common.facebook;

import android.support.annotation.NonNull;

import com.facebook.FacebookException;

public class FacebookRequestException extends RuntimeException
{
//    @NonNull public final FacebookRequestError facebookCause;
@NonNull public final FacebookException facebookException;

    //<editor-fold desc="Constructors">
    public FacebookRequestException(@NonNull FacebookException facebookException)
    {
        this.facebookException = facebookException;
    }

    public FacebookRequestException(String detailMessage, @NonNull FacebookException facebookException)
    {
        super(detailMessage);
        this.facebookException = facebookException;
    }
    //</editor-fold>
}
