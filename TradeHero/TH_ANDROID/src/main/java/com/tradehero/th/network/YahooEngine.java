package com.tradehero.th.network;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tradehero.common.utils.JacksonConverter;
import com.tradehero.th.R;
import com.tradehero.th.base.Application;
import com.tradehero.th.base.THUser;
import retrofit.ErrorHandler;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.converter.Converter;

/**
 * Created by julien on 10/10/13
 */
public class YahooEngine
{
    private final String API_URL = Application.getResourceString(R.string.YAHOO_FINANCE_URL);

    private RestAdapter restAdapter;
    private ErrorHandler retrofitErrorHandler = new ErrorHandler()
    {
        @Override public Throwable handleError(RetrofitError cause)
        {
            return cause;
        }
    };

    private static YahooEngine instance = new YahooEngine();

    public YahooEngine()
    {
        initialize();
    }

    public void initialize()
    {
        Converter converter = new JacksonConverter(new ObjectMapper());
        restAdapter = new RestAdapter.Builder()
                .setServer(API_URL)
                .setConverter(converter)
                .setErrorHandler(retrofitErrorHandler)
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();
    }


    public <T> T createService(Class<T> service)
    {
        return restAdapter.create(service);
    }

    public static YahooEngine getInstance()
    {
        return instance;
    }
}
