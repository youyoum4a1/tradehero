package com.ayondo.academyapp.models.sms.twilio;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import retrofit.RetrofitError;

public class TwilioRetrofitException extends RuntimeException
{
    @NonNull public final RetrofitError retrofitError;
    @Nullable public final TwilioErrorMessage errorMessage;

    public TwilioRetrofitException(@NonNull RetrofitError retrofitError)
    {
        super(retrofitError);
        this.retrofitError = retrofitError;
        this.errorMessage = getErrorMessage(retrofitError);
    }

    public TwilioRetrofitException(String detailMessage, @NonNull RetrofitError retrofitError)
    {
        super(detailMessage, retrofitError);
        this.retrofitError = retrofitError;
        this.errorMessage = getErrorMessage(retrofitError);
    }

    @Nullable public static TwilioErrorMessage getErrorMessage(@NonNull RetrofitError retrofitError)
    {
        try
        {
            return (TwilioErrorMessage) retrofitError.getBodyAs(TwilioErrorMessage.class);
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
