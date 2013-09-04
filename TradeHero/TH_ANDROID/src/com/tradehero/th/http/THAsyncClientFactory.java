package com.tradehero.th.http;

import com.tradehero.th.base.THUser;
import java.util.HashMap;
import java.util.Map;

import com.tradehero.th.application.App;
import com.tradehero.th.models.Token;
import com.tradehero.th.utills.Constants;
import android.util.Base64;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;

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
        private final Token tokenWrapper;

        public THAsyncClient(String authorizationType)
        {
            this.authorizationType = authorizationType;
            this.tokenWrapper = new Token(THUser.getSessionToken());
            injectAuthenticationHeader();
            setTimeout(DEFAULT_TIMEOUT);
        }

        private void injectAuthenticationHeader()
        {
            String authToken = Base64.encodeToString(tokenWrapper.getToken().getBytes(), Base64.NO_WRAP);
            addHeader(Constants.TH_CLIENT_VERSION, Constants.TH_CLIENT_VERSION_VALUE);
            addHeader(Constants.AUTHORIZATION,
                    String.format("%s %s", authorizationType, authToken));
            addHeader("Content-type", "application/json");
        }
    }
}