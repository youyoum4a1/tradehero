package com.androidth.general.models.fastfill.jumio;

//import com.squareup.okhttp.Interceptor;
//import com.squareup.okhttp.Request;
//import com.squareup.okhttp.Response;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

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
