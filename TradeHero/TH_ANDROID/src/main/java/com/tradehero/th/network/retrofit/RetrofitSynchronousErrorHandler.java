package com.tradehero.th.network.retrofit;

import javax.inject.Inject;
import retrofit.ErrorHandler;
import retrofit.RetrofitError;

public class RetrofitSynchronousErrorHandler implements ErrorHandler
{
    @Inject public RetrofitSynchronousErrorHandler()
    {
        super();
    }

    @Override public Throwable handleError(RetrofitError cause)
    {
        return cause;
    }
}
