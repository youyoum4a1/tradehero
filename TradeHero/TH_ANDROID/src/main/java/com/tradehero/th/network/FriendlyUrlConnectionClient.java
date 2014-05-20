package com.tradehero.th.network;

import android.content.Context;
import com.tradehero.th.utils.NetworkUtils;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.net.ssl.HttpsURLConnection;
import retrofit.client.Request;
import retrofit.client.UrlConnectionClient;
import timber.log.Timber;

@Singleton
public class FriendlyUrlConnectionClient extends UrlConnectionClient
{
    private final Context context;

    @Inject public FriendlyUrlConnectionClient(Context context)
    {
        super();
        this.context = context;

        HttpsURLConnection.setDefaultHostnameVerifier(new NullHostNameVerifier());
        enableHttpResponseCache();
    }

    /**
     * Enable http cache
     */
    private void enableHttpResponseCache()
    {
        try
        {
            long httpCacheSize = 10 * 1024 * 1024; // 10 MiB
            File httpCacheDir = new File(context.getCacheDir(), "http");
            Class.forName("android.net.http.HttpResponseCache")
                    .getMethod("install", File.class, long.class)
                    .invoke(null, httpCacheDir, httpCacheSize);
        }
        catch (Exception httpResponseCacheNotAvailable)
        {
            Timber.e("Response cache is not available", httpResponseCacheNotAvailable);
        }
    }

    @Override protected HttpURLConnection openConnection(Request request) throws IOException
    {
        HttpURLConnection connection = super.openConnection(request);
        if (connection instanceof HttpsURLConnection)
        {
            ((HttpsURLConnection) connection).setSSLSocketFactory(NetworkUtils.createBadSslSocketFactory());
        }
        return connection;
    }
}
