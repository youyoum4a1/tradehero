package com.tradehero.th.http;

import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;

import android.content.Context;
import android.net.SSLCertificateSocketFactory;
import android.net.SSLSessionCache;

public class HttpClientUtil
{

    private static final int CONNECTION_TIMEOUT = 5000;
    private static final int SOCKET_TIMEOUT = 20000;

    /**
     * @param context
     * @return
     */
    static HttpClient getHttpClient(Context context)
    {
        return getHttpClient(context, CONNECTION_TIMEOUT, SOCKET_TIMEOUT);
    }

    /**
     * @param context
     * @param connectionTimeOut
     * @param sockeTimeOut
     * @return
     */
    static synchronized HttpClient getHttpClient(Context context, int connectionTimeOut,
            int sockeTimeOut)
    {

        // Use a session cache for SSL sockets
        SSLSessionCache sessionCache = context == null ? null : new SSLSessionCache(context);

        // sets up parameters
        HttpParams httpParams = new BasicHttpParams();

        setHttpProtocolParams(httpParams);
        setHttpConnectionParams(httpParams, connectionTimeOut, sockeTimeOut);

        httpParams.setBooleanParameter("http.protocol.expect-continue", false);

        // registers schemes for both http and https
        SchemeRegistry registry = new SchemeRegistry();
        registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));

        final SSLSocketFactory sslSocketFactory =
                SSLCertificateSocketFactory.getHttpSocketFactory(sockeTimeOut,
                        sessionCache);

        // sslSocketFactory.setHostnameVerifier(SSLSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
        sslSocketFactory.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

        registry.register(new Scheme("https", sslSocketFactory, 443));

        ThreadSafeClientConnManager manager = new ThreadSafeClientConnManager(httpParams, registry);

        return new DefaultHttpClient(manager, httpParams);
    }

    /**
     * @param httpParams
     */
    private static void setHttpProtocolParams(HttpParams httpParams)
    {
        HttpProtocolParams.setVersion(httpParams, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(httpParams, "utf-8");
    }

    /**
     * @param httpParams
     * @param connectionTimeOut
     * @param sockeTimeOut
     */
    private static void setHttpConnectionParams(HttpParams httpParams, int connectionTimeOut,
            int sockeTimeOut)
    {
        HttpConnectionParams.setConnectionTimeout(httpParams, connectionTimeOut);
        HttpConnectionParams.setSoTimeout(httpParams, sockeTimeOut);
    }
}
