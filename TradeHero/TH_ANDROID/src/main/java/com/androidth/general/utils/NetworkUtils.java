package com.androidth.general.utils;

import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
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

            X509TrustManager origTrustManager = getTrustManager();

            if(origTrustManager==null){
                throw new AssertionError();
            }

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

    public static X509TrustManager getTrustManager(){

        try{
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(
                    TrustManagerFactory.getDefaultAlgorithm());
// Initialise the TMF as you normally would, for example:
            tmf.init((KeyStore)null);
            TrustManager[] trustManagers = tmf.getTrustManagers();

            final X509TrustManager origTrustManager = (X509TrustManager)trustManagers[0];

            return origTrustManager;

        }catch (NoSuchAlgorithmException e){
            e.printStackTrace();
        }catch (KeyStoreException e){
            e.printStackTrace();
        }

        return null;

    }

}
