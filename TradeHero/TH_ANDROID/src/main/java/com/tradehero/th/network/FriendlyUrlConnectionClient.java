package com.tradehero.th.network;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import retrofit.client.Request;
import retrofit.client.UrlConnectionClient;

/**
 * Created with IntelliJ IDEA. User: tho Date: 2/17/14 Time: 12:30 PM Copyright (c) TradeHero
 */
@Singleton
public class FriendlyUrlConnectionClient extends UrlConnectionClient
{
    @Inject public FriendlyUrlConnectionClient()
    {
        super();
    }

    @Override protected HttpURLConnection openConnection(Request request) throws IOException
    {
        HttpURLConnection connection = super.openConnection(request);
        if (connection instanceof HttpsURLConnection)
        {
            ((HttpsURLConnection) connection).setSSLSocketFactory(createBadSslSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(new NullHostNameVerifier());
        }
        return connection;
    }

    private static SSLSocketFactory createBadSslSocketFactory()
    {
        try
        {
            SSLContext context = SSLContext.getInstance("TLS");
            TrustManager permissive = new X509TrustManager()
            {
                @Override public void checkClientTrusted(X509Certificate[] chain, String authType)
                        throws CertificateException
                {
                }

                @Override public void checkServerTrusted(X509Certificate[] chain, String authType)
                        throws CertificateException
                {
                }

                @Override public X509Certificate[] getAcceptedIssuers()
                {
                    return null;
                }
            };
            context.init(null, new TrustManager[] {permissive}, new SecureRandom());
            return context.getSocketFactory();
        }
        catch (Exception e)
        {
            throw new AssertionError(e);
        }
    }
}
