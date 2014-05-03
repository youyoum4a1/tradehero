package com.tradehero.th.auth;

import android.content.Context;
import com.tradehero.th.auth.operator.LinkedIn;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.json.JSONException;
import org.json.JSONObject;


@Singleton
public class LinkedInAuthenticationProvider extends SocialAuthenticationProvider
{
    private static final String AUTH_TOKEN_SECRET_KEY = "auth_token_secret";
    private static final String AUTH_TOKEN_KEY = "auth_token";
    private static final String CONSUMER_KEY_KEY = "consumer_key";
    private static final String CONSUMER_SECRET_KEY = "consumer_secret";
    private final LinkedIn linkedIn;

    @Inject public LinkedInAuthenticationProvider(LinkedIn linkedIn)
    {
        this.linkedIn = linkedIn;
    }

    @Override public void authenticate(final THAuthenticationCallback callback)
    {
        if (currentOperationCallback != null)
        {
            cancel();
        }
        currentOperationCallback = callback;

        Context context = baseContext == null ? null : baseContext.get();
        if (context == null)
        {
            throw new IllegalStateException("Context must be non-null for LinkedIn authentication to proceed.");
        }

        linkedIn.authorize(context, new THAuthenticationCallback()
        {
            public void onCancel()
            {
                LinkedInAuthenticationProvider.this.handleCancel(callback);
            }

            public void onError(Throwable error)
            {
                if (LinkedInAuthenticationProvider.this.currentOperationCallback != callback)
                {
                    return;
                }
                try
                {
                    callback.onError(error);
                }
                finally
                {
                    LinkedInAuthenticationProvider.this.currentOperationCallback = null;
                }
            }

            @Override
            public void onStart()
            {
                callback.onStart();
            }

            @Override
            public void onSuccess(JSONObject result)
            {
                if (LinkedInAuthenticationProvider.this.currentOperationCallback != callback)
                {
                    return;
                }
                try
                {
                    JSONObject authData;
                    try
                    {
                        authData = LinkedInAuthenticationProvider.this.getAuthData(
                                LinkedInAuthenticationProvider.this.linkedIn.getAuthToken(),
                                LinkedInAuthenticationProvider.this.linkedIn.getAuthTokenSecret());
                    }
                    catch (JSONException e)
                    {
                        callback.onError(e);
                        return;
                    }
                    callback.onSuccess(authData);
                }
                finally
                {
                    LinkedInAuthenticationProvider.this.currentOperationCallback = null;
                }
            }
        });
    }

    @Override public void deauthenticate()
    {
        linkedIn.setAuthToken(null);
        linkedIn.setAuthTokenSecret(null);
    }

    @Override public boolean restoreAuthentication(JSONObject authData)
    {
        if (authData == null)
        {
            deauthenticate();
            return true;
        }
        try
        {
            linkedIn.setAuthToken(authData.getString(AUTH_TOKEN_KEY));
            linkedIn.setAuthTokenSecret(authData.getString(AUTH_TOKEN_SECRET_KEY));

            return true;
        }
        catch (Exception e)
        {
        }
        return false;
    }

    public String getAuthType()
    {
        return SocialAuthenticationProvider.LINKEDIN_AUTH_TYPE;
    }

    @Override public String getAuthHeaderParameter()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(linkedIn.getAuthToken()).append(":").append(linkedIn.getAuthTokenSecret());
        return  sb.toString();
    }

    public JSONObject getAuthData(String authToken, String authTokenSecret)
            throws JSONException
    {
        JSONObject authData = new JSONObject();
        authData.put(AUTH_TOKEN_KEY, authToken);
        authData.put(AUTH_TOKEN_SECRET_KEY, authTokenSecret);
        authData.put(CONSUMER_KEY_KEY, linkedIn.getConsumerKey());
        authData.put(CONSUMER_SECRET_KEY, linkedIn.getConsumerSecret());
        return authData;
    }
}
