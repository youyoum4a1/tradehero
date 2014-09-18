package com.tradehero.th.network;

import com.squareup.okhttp.Authenticator;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import java.io.IOException;
import java.net.Proxy;
import javax.inject.Inject;
import javax.inject.Singleton;
import timber.log.Timber;

@Singleton
public class ApiAuthenticator implements Authenticator
{
    @Inject ApiAuthenticator()
    {
    }

    @Override public Request authenticate(Proxy proxy, Response response) throws IOException
    {
        Timber.d("Not authenticated, need re-authentication");

        // TODO if there is an authentication token, try to extend the token
        // TODO if token cannot be extended, invalidate the token
        // TODO after that, request credential from AccountManager, which activate the addAccount method and consequentially will halt the current
        // TODO activity and open up activity for authentication
        return null;
    }

    @Override public Request authenticateProxy(Proxy proxy, Response response) throws IOException
    {
        return null;
    }
}
