package com.androidth.general.models.sms.twilio;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.androidth.general.models.retrofit2.THRetrofitException;

public class TwilioRetrofitException extends RuntimeException
{
    @NonNull public final THRetrofitException retrofitError;
    @Nullable public final TwilioErrorMessage errorMessage;

    public TwilioRetrofitException(@NonNull THRetrofitException retrofitError)
    {
        super(retrofitError);
        this.retrofitError = retrofitError;
        this.errorMessage = getErrorMessage(retrofitError);
    }

    public TwilioRetrofitException(String detailMessage, @NonNull THRetrofitException retrofitError)
    {
        super(detailMessage, retrofitError);
        this.retrofitError = retrofitError;
        this.errorMessage = getErrorMessage(retrofitError);
    }

    @Nullable public static TwilioErrorMessage getErrorMessage(@NonNull THRetrofitException retrofitError)
    {
        try
        {
            return (TwilioErrorMessage) retrofitError.getErrorBodyAs(TwilioErrorMessage.class);
        } catch (Exception e)
        {
            return null;
        }
    }

    @Override public String getMessage()
    {
        if (errorMessage != null)
        {
            return errorMessage.toString();
        }
        return super.getMessage();
    }
}
