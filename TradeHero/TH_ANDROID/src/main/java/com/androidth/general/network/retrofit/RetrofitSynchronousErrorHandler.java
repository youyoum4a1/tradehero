package com.androidth.general.network.retrofit;

import javax.inject.Inject;
import retrofit.ErrorHandler;
import retrofit.RetrofitError;

public class RetrofitSynchronousErrorHandler implements ErrorHandler
{
    //<editor-fold desc="Constructors">
    @Inject public RetrofitSynchronousErrorHandler()
    {
        super();
    }
    //</editor-fold>

    @Override public Throwable handleError(RetrofitError cause)
    {
        return cause;
    }
}
