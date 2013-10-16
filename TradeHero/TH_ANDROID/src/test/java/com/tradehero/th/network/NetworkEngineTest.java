package com.tradehero.th.network;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tradehero.common.utils.JacksonConverter;
import com.tradehero.th.base.THUser;
import retrofit.ErrorHandler;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.converter.Converter;

/** Created with IntelliJ IDEA. User: tho Date: 10/14/13 Time: 4:22 PM Copyright (c) TradeHero */
public class NetworkEngineTest
{
    private static final String API_URL = "https://www.tradehero.mobi/api";

    private RestAdapter restAdapter;
    private ErrorHandler retrofitErrorHandler = new ErrorHandler()
    {
        @Override public Throwable handleError(RetrofitError cause)
        {
            return cause;
        }
    };

    private static NetworkEngineTest instance = new NetworkEngineTest();

    public NetworkEngineTest()
    {
        initialize();
    }

    public void initialize()
    {
        Converter converter = new JacksonConverter(new ObjectMapper());
        restAdapter = new RestAdapter.Builder()
                .setServer(API_URL)
                .setConverter(converter)
                .setRequestInterceptor(new RequestInterceptor()
                {
                    @Override
                    public void intercept(RequestFacade request)
                    {
                        buildAuthorizationHeader(request);
                    }
                })
                .setErrorHandler(retrofitErrorHandler)
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();
    }

    private void buildAuthorizationHeader(RequestInterceptor.RequestFacade request)
    {
        request.addHeader("TH-Client-Version", "1.5.3.3016");
        // neeraj
        request.addHeader("Authorization", "Basic bmVlcmFqQGVhdGVjaG5vbG9naWVzLmNvbTp0ZXN0aW5n");
    }

    public <T> T createService(Class<T> service)
    {
        return restAdapter.create(service);
    }

    public static NetworkEngineTest getInstance()
    {
        return instance;
    }
}
