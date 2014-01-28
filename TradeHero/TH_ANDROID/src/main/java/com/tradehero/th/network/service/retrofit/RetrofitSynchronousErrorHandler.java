package com.tradehero.th.network.service.retrofit;

import javax.inject.Inject;
import retrofit.ErrorHandler;
import retrofit.RetrofitError;

/**
 * Created with IntelliJ IDEA. User: tho Date: 1/27/14 Time: 11:16 AM Copyright (c) TradeHero
 */
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
