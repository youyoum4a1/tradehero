package com.androidth.general.common.facebook;

import android.support.annotation.NonNull;
import com.facebook.FacebookRequestError;

public class FacebookRequestException extends RuntimeException
{
    @NonNull public final FacebookRequestError facebookCause;

    //<editor-fold desc="Constructors">
    public FacebookRequestException(@NonNull FacebookRequestError facebookCause)
    {
        this.facebookCause = facebookCause;
    }

    public FacebookRequestException(String detailMessage, @NonNull FacebookRequestError facebookCause)
    {
        super(detailMessage);
        this.facebookCause = facebookCause;
    }
    //</editor-fold>
}
