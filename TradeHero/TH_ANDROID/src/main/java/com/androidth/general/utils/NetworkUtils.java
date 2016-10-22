package com.androidth.general.utils;

import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

public final class NetworkUtils
{
    public static SSLSocketFactory createBadSslSocketFactory()
    {
        try
        {
            SSLContext context = SSLContext.getInstance("TLS");
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(
                    TrustManagerFactory.getDefaultAlgorithm());
// Initialise the TMF as you normally would, for example:
            tmf.init((KeyStore)null);
            TrustManager[] trustManagers = tmf.getTrustManagers();

            final X509TrustManager origTrustManager = (X509TrustManager)trustManagers[0];

//            TrustManager[] wrappedTrustManagers =
//
//            X509TrustManager permissive = new X509TrustManager()
//            {
//                @Override public void checkClientTrusted(X509Certificate[] chain, String authType)
//                        throws CertificateException
//
//                {
//                }
//
//                @Override public void checkServerTrusted(X509Certificate[] chain, String authType)
//                        throws CertificateException
//                {
//                    try {
//                        chain[0].checkValidity();
//                    } catch (Exception e) {
//                        throw new CertificateException("Certificate not valid or trusted.");
//                    }
//                }
//
//                @Override public X509Certificate[] getAcceptedIssuers()
//                {
////                    return null;
//                    return this.getAcceptedIssuers();
//                }
//            };
////            context.init(null, new TrustManager[] {permissive}, new SecureRandom());


            TrustManager[] wrappedTrustManagers = new TrustManager[]{
                    new X509TrustManager() {
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return origTrustManager.getAcceptedIssuers();
                        }

                        public void checkClientTrusted(X509Certificate[] certs, String authType) throws CertificateException {
                            origTrustManager.checkClientTrusted(certs, authType);
                        }

                        public void checkServerTrusted(X509Certificate[] certs, String authType) throws CertificateException{
                            try {
                                origTrustManager.checkServerTrusted(certs, authType);
                            } catch (CertificateExpiredException e) {}
                        }
                    }
            };

            context.init(null, wrappedTrustManagers, null);
            return context.getSocketFactory();
        }
        catch (Exception e)
        {
            throw new AssertionError(e);
        }
    }


}
