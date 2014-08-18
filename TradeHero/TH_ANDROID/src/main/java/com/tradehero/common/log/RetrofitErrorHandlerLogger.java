package com.tradehero.common.log;

import java.net.SocketTimeoutException;
import retrofit.ErrorHandler;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

public class RetrofitErrorHandlerLogger implements ErrorHandler
{
    @Override public Throwable handleError(RetrofitError retrofitError)
    {
        if (retrofitError != null)
        {
            Throwable cause = retrofitError.getCause();
            if (cause instanceof SocketTimeoutException)
            {
                // Attempt at reporting server sluggishness
                Timber.e(cause, "Retrofit timeout on %s", retrofitError.getUrl());
            }
            else if (retrofitError.isNetworkError())
            {
                Response response = retrofitError.getResponse();
                Integer responseCode = null;
                if (response != null)
                {
                    responseCode = response.getStatus();
                }
                if (responseCode != null)
                {
                    Timber.e(retrofitError, "Response Code %d on %s", responseCode, retrofitError.getUrl());
                }
            }
        }
        return retrofitError;
    }
}
