package com.ayondo.academy.models.fastfill.jumio;

import retrofit.RequestInterceptor;

public class NetverifyRetrofitRequestInterceptor implements RequestInterceptor
{
    @Override public void intercept(RequestFacade request)
    {
        request.addHeader("Authorization", NetverifyConstants.NETVERIFY_AUTH_HEADER);
        request.addHeader("Accept", "application/json");
        request.addHeader("User-Agent", NetverifyConstants.NETVERIFY_USER_AGENT);
    }
}
