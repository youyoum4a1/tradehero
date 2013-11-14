package com.tradehero.th.http;

import android.util.Log;
import com.loopj.android.http.AsyncHttpClient;
import com.tradehero.th.base.THUser;
import com.tradehero.th.utils.Constants;
import java.util.HashMap;
import java.util.Map;

public abstract class THAsyncClientFactory
{
    private static final String TAG = THAsyncClientFactory.class.getName();
    private static Map<String, AsyncHttpClient> clients = new HashMap<String, AsyncHttpClient>();

    public static AsyncHttpClient getInstance(String authorizationType)
    {
        // make sure we have correct authorization type
        if (authorizationType == null || !(
                authorizationType.equals(Constants.TH_EMAIL_PREFIX) ||
                authorizationType.equals(Constants.TH_TWITTER_PREFIX) ||
                authorizationType.equals(Constants.TH_FB_PREFIX) ||
                authorizationType.equals(Constants.TH_LINKEDIN_PREFIX)))
        {
            Log.e(TAG, "Wrong authorization type");
        }
        AsyncHttpClient client = clients.get(authorizationType);
        if (client == null)
        {
            client = new THAsyncClient(authorizationType);
            clients.put(authorizationType, client);
        }
        return client;
    }

    private static class THAsyncClient extends AsyncHttpClient
    {
        final int DEFAULT_TIMEOUT = 20 * 1000;
        private final String authorizationType;

        public THAsyncClient(String authorizationType)
        {
            this.authorizationType = authorizationType;
            injectAuthenticationHeader();
            setTimeout(DEFAULT_TIMEOUT);
        }

        private void injectAuthenticationHeader()
        {
            String authToken = THUser.getSessionToken();
            addHeader(Constants.TH_CLIENT_VERSION, Constants.TH_CLIENT_VERSION_VALUE);
            addHeader(Constants.AUTHORIZATION,
                    String.format("%s %s", authorizationType, authToken));
            addHeader("Content-type", "application/json");
        }
    }
}