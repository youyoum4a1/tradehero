package com.tradehero.th.network;

/**
 * Created with IntelliJ IDEA. User: tho Date: 8/14/13 Time: 6:13 PM To change this template use
 * File | Settings | File Templates.
 */

import com.tradehero.common.utils.THJsonAdapter;
import com.tradehero.th.R;
import com.tradehero.th.application.App;
import com.tradehero.th.base.THUser;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;

public class NetworkEngine
{
    private static final String API_URL = App.getResourceString(R.string.API_URL);
    private static final String API_FACEBOOK_TOKEN_HEADER = "TH-Facebook";

    private static RestAdapter restAdapter;

    public static void initialize()
    {
        restAdapter = new RestAdapter.Builder()
                .setServer(API_URL)
                .setConverter(THJsonAdapter.getInstance())
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
                .build();
    }

    private static void buildAuthorizationHeader(RequestInterceptor.RequestFacade request)
    {
        request.addHeader("TH-Client-Version", "1.5.2");
        request.addHeader("Authorization", THUser.getAuthenticationHeader());
    }

    public static <T> T createService(Class<T> service)
    {
        return restAdapter.create(service);
    }
}
