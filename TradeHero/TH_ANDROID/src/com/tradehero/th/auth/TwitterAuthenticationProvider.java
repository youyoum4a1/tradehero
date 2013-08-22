package com.tradehero.th.auth;

import android.content.Context;
import com.tradehero.th.auth.operator.Twitter;
import org.json.JSONException;
import org.json.JSONObject;

public class TwitterAuthenticationProvider extends SocialAuthenticationProvider
{
    private final Twitter twitter;

    public TwitterAuthenticationProvider(Twitter twitter)
    {
        this.twitter = twitter;
    }

    @Override public void authenticate(final THAuthenticationProvider.THAuthenticationCallback callback)
    {
        if (currentOperationCallback != null)
        {
            cancel();
        }
        currentOperationCallback = callback;

        Context context = baseContext == null ? null : baseContext.get();
        if (context == null)
        {
            throw new IllegalStateException(
                    "Context must be non-null for Twitter authentication to proceed.");
        }

        twitter.authorize(context, new THAuthenticationCallback()
        {
            @Override public void onCancel()
            {
                TwitterAuthenticationProvider.this.handleCancel(callback);
            }

            @Override public void onError(Throwable error)
            {
                if (TwitterAuthenticationProvider.this.currentOperationCallback != callback)
                {
                    return;
                }
                try
                {
                    callback.onError(error);
                }
                finally
                {
                    TwitterAuthenticationProvider.this.currentOperationCallback = null;
                }
            }

            @Override public void onStart()
            {
                callback.onStart();
            }

            @Override public void onSuccess(JSONObject result)
            {
                if (TwitterAuthenticationProvider.this.currentOperationCallback != callback)
                {
                    return;
                }
                try
                {
                    JSONObject authData;
                    try
                    {
                        authData = TwitterAuthenticationProvider.this.getAuthData(
                                TwitterAuthenticationProvider.this.twitter.getUserId(),
                                TwitterAuthenticationProvider.this.twitter.getScreenName(),
                                TwitterAuthenticationProvider.this.twitter.getAuthToken(),
                                TwitterAuthenticationProvider.this.twitter.getAuthTokenSecret());
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
                    TwitterAuthenticationProvider.this.currentOperationCallback = null;
                }
            }
        });
    }

    public JSONObject getAuthData(String userId, String screenName, String authToken, String authTokenSecret)
            throws JSONException
    {
        JSONObject authData = new JSONObject();
        authData.put("type", getAuthType());
        authData.put(AUTH_TOKEN_KEY, authToken);
        authData.put(AUTH_TOKEN_SECRET_KEY, authTokenSecret);
        authData.put(ID_KEY, userId);
        authData.put(SCREEN_NAME_KEY, screenName);
        authData.put(CONSUMER_KEY_KEY, twitter.getConsumerKey());
        authData.put(CONSUMER_SECRET_KEY, twitter.getConsumerSecret());
        return authData;
    }

    @Override public void deauthenticate()
    {
        twitter.setAuthToken(null);
        twitter.setAuthTokenSecret(null);
        twitter.setScreenName(null);
        twitter.setUserId(null);
    }

    @Override public String getAuthType()
    {
        return "twitter";
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
            twitter.setAuthToken(authData.getString(AUTH_TOKEN_KEY));
            twitter.setAuthTokenSecret(authData.getString(AUTH_TOKEN_SECRET_KEY));
            twitter.setUserId(authData.getString(ID_KEY));
            twitter.setScreenName(authData.getString(SCREEN_NAME_KEY));

            return true;
        }
        catch (Exception e)
        {
        }
        return false;
    }
}