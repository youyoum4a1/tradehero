package com.androidth.general.models.fastfill.jumio;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

import okhttp3.Interceptor;

public class NetverifyPicassoRequestInterceptor implements Interceptor
{
    @Override public Response intercept(Interceptor.Chain chain) throws IOException
    {
        Request modifiedRequest = chain.request()
                .newBuilder()
                .addHeader("Authorization", NetverifyConstants.NETVERIFY_AUTH_HEADER)
                .addHeader("User-Agent", NetverifyConstants.NETVERIFY_USER_AGENT)
                .build();
        return chain.proceed(modifiedRequest);
    }
}
