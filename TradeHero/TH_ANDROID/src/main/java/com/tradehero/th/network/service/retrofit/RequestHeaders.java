package com.tradehero.th.network.service.retrofit;

import com.tradehero.th.base.THUser;
import com.tradehero.th.utils.Constants;
import javax.inject.Inject;
import retrofit.RequestInterceptor;

/**
 * Created with IntelliJ IDEA. User: tho Date: 1/27/14 Time: 11:10 AM Copyright (c) TradeHero
 */

public class RequestHeaders implements RequestInterceptor
{
    @Inject public RequestHeaders()
    {
        super();
    }

    @Override
    public void intercept(RequestFacade request)
    {
        if (THUser.hasSessionToken())
        {
            buildAuthorizationHeader(request);
        }
    }

    private void buildAuthorizationHeader(RequestInterceptor.RequestFacade request)
    {
        request.addHeader(Constants.TH_CLIENT_VERSION, Constants.TH_CLIENT_VERSION_VALUE);
        request.addHeader(Constants.AUTHORIZATION, THUser.getAuthHeader());
    }
}