package com.tradehero.th.network.retrofit;

import com.tradehero.common.persistence.prefs.StringPreference;
import com.tradehero.th.base.THUser;
import com.tradehero.th.persistence.prefs.SessionToken;
import com.tradehero.th.utils.Constants;
import javax.inject.Inject;
import retrofit.RequestInterceptor;
import timber.log.Timber;

/**
 * Created with IntelliJ IDEA. User: tho Date: 1/27/14 Time: 11:10 AM Copyright (c) TradeHero
 */

public class RequestHeaders implements RequestInterceptor
{
    @Inject @SessionToken StringPreference currentSessionToken;

    @Inject public RequestHeaders()
    {
        super();
    }

    @Override
    public void intercept(RequestFacade request)
    {
        if (currentSessionToken.isSet())
        {
            buildAuthorizationHeader(request);
        }
    }

    private void buildAuthorizationHeader(RequestInterceptor.RequestFacade request)
    {
        request.addHeader(Constants.TH_CLIENT_VERSION, Constants.TH_CLIENT_VERSION_VALUE);
        request.addHeader(Constants.AUTHORIZATION, THUser.getAuthHeader());
        Timber.d("buildAuthorizationHeader AUTHORIZATION: %s",THUser.getAuthHeader());
    }
}