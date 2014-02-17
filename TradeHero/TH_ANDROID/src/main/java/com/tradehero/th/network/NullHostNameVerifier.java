package com.tradehero.th.network;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

/**
 * Created with IntelliJ IDEA. User: tho Date: 2/17/14 Time: 12:28 PM Copyright (c) TradeHero
 */
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