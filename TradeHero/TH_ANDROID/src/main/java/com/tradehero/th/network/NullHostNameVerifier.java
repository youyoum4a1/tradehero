package com.tradehero.th.network;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;


@Singleton
public class NullHostNameVerifier implements HostnameVerifier
{
    @Inject public NullHostNameVerifier()
    {
    }

    public boolean verify(String hostname, SSLSession session)
    {
        return true;
    }
}