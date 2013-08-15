package com.tradehero.th.network;

/**
 * Created with IntelliJ IDEA. User: tho Date: 8/14/13 Time: 6:13 PM To change this template use
 * File | Settings | File Templates.
 */

import com.tradehero.kit.utils.THJsonAdapter;
import com.tradehero.th.R;
import com.tradehero.th.application.App;
import com.tradehero.th.base.THUser;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;

public class NetworkEngine
{
    private static final String API_URL = App.getResourceString(R.string.API_URL);
    private static final String API_FACEBOOK_TOKEN_HEADER = "TH-Facebook";

    private static NetworkEngine instance = null;
    private RestAdapter restAdapter;

    private NetworkEngine()
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

    private final void buildAuthorizationHeader(RequestInterceptor.RequestFacade request)
    {
        StringBuilder sb = new StringBuilder();
        sb.append(API_FACEBOOK_TOKEN_HEADER);
        sb.append(" ");
        sb.append(THUser.getSessionToken());

        request.addHeader("Authorization", sb.toString());
    }

    public static NetworkEngine getInstance()
    {
        if (instance == null)
        {
            instance = new NetworkEngine();
        }
        return instance;
    }

    public RestAdapter getRestAdapter()
    {
        return restAdapter;
    }
}
