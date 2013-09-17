package com.tradehero.th.network;

/**
 * Created with IntelliJ IDEA. User: tho Date: 8/14/13 Time: 6:13 PM To change this template use
 * File | Settings | File Templates.
 */

import com.tradehero.th.R;
import com.tradehero.th.application.App;
import com.tradehero.th.base.THUser;
import javax.inject.Inject;
import retrofit.ErrorHandler;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.converter.Converter;

public class NetworkEngine
{
    private static final String API_URL = App.getResourceString(R.string.API_URL);

    private static RestAdapter restAdapter;
    private static ErrorHandler retrofitErrorHandler = new ErrorHandler()
    {
        @Override public Throwable handleError(RetrofitError cause)
        {
            return cause;
        }
    };

    @Inject
    static Converter converter;

    public static void initialize()
    {
        restAdapter = new RestAdapter.Builder()
                .setServer(API_URL)
                .setConverter(converter)
                .setRequestInterceptor(new RequestInterceptor()
                {
                    @Override
                    public void intercept(RequestFacade request)
                    {
                        if (THUser.hasSessionToken())
                        {
                            buildAuthorizationHeader(request);
                        }
                    }
                })
                .setErrorHandler(retrofitErrorHandler)
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();
    }

    private static void buildAuthorizationHeader(RequestInterceptor.RequestFacade request)
    {
        request.addHeader("TH-Client-Version", "1.5.3.3016");
        request.addHeader("Authorization", THUser.getAuthHeader());
    }

    public static <T> T createService(Class<T> service)
    {
        return restAdapter.create(service);
    }
}
