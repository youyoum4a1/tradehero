package com.androidth.general.models.fastfill.jumio;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class NetverifyRetrofitRequestInterceptor implements Interceptor
{
//    @Override public void intercept(RequestFacade request)
//    {
//        request.addHeader("Authorization", NetverifyConstants.NETVERIFY_AUTH_HEADER);
//        request.addHeader("Accept", "application/json");
//        request.addHeader("User-Agent", NetverifyConstants.NETVERIFY_USER_AGENT);
//    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request.Builder request = chain.request().newBuilder();
        request.addHeader("Authorization", NetverifyConstants.NETVERIFY_AUTH_HEADER);
        request.addHeader("Accept", "application/json");
        request.addHeader("User-Agent", NetverifyConstants.NETVERIFY_USER_AGENT);

        return chain.proceed(request.build());
    }
}
