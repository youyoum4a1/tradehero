package com.tradehero.th.auth;

import android.content.Context;
import com.tradehero.th.auth.twitter.Twitter;
import java.lang.ref.WeakReference;
import org.json.JSONException;
import org.json.JSONObject;

public class TwitterAuthenticationProvider
        implements THAuthenticationProvider
{
    private static final String SCREEN_NAME_KEY = "screen_name";
    private static final String ID_KEY = "id";
    private static final String AUTH_TOKEN_SECRET_KEY = "auth_token_secret";
    private static final String AUTH_TOKEN_KEY = "auth_token";
    private static final String CONSUMER_KEY_KEY = "consumer_key";
    private static final String CONSUMER_SECRET_KEY = "consumer_secret";
    private WeakReference<Context> baseContext;
    private final Twitter twitter;
    private THAuthenticationProvider.THAuthenticationCallback currentOperationCallback;

    public TwitterAuthenticationProvider(Twitter twitter)
    {
        this.twitter = twitter;
    }

    public void authenticate(final THAuthenticationProvider.THAuthenticationCallback callback)
    {
        if (this.currentOperationCallback != null)
        {
            cancel();
        }
        this.currentOperationCallback = callback;

        Context context = this.baseContext == null ? null : (Context) this.baseContext.get();
        if (context == null)
        {
            throw new IllegalStateException(
                    "Context must be non-null for Twitter authentication to proceed.");
        }

        this.twitter.authorize(context, new THAuthenticationCallback()
        {
            public void onCancel()
            {
                TwitterAuthenticationProvider.this.handleCancel(callback);
            }

            public void onError(Throwable error)
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

            @Override
            public void onStart()
            {
                callback.onStart();
            }

            @Override
            public void onSuccess(JSONObject result)
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

    public JSONObject getAuthData(String userId, String screenName, String authToken,
            String authTokenSecret)
            throws JSONException
    {
        JSONObject authData = new JSONObject();
        authData.put("type", "twitter");
        authData.put("auth_token", authToken);
        authData.put("auth_token_secret", authTokenSecret);
        authData.put("id", userId);
        authData.put("screen_name", screenName);
        authData.put("consumer_key", this.twitter.getConsumerKey());
        authData.put("consumer_secret", this.twitter.getConsumerSecret());
        return authData;
    }

    @Override public void cancel()
    {
        handleCancel(this.currentOperationCallback);
    }

    public void deauthenticate()
    {
        // deactivate all the keys
    }

    public String getAuthType()
    {
        return "twitter";
    }

    private void handleCancel(THAuthenticationProvider.THAuthenticationCallback callback)
    {
        if ((this.currentOperationCallback != callback) || (callback == null))
        {
            return;
        }
        try
        {
            callback.onCancel();
        }
        finally
        {
            this.currentOperationCallback = null;
        }
    }

    public boolean restoreAuthentication(JSONObject authData)
    {
        if (authData == null)
        {
            deauthenticate();
            return true;
        }
        try
        {
            this.twitter.setAuthToken(authData.getString("auth_token"));
            this.twitter.setAuthTokenSecret(authData.getString("auth_token_secret"));
            this.twitter.setUserId(authData.getString("id"));
            this.twitter.setScreenName(authData.getString("screen_name"));

            return true;
        }
        catch (Exception e)
        {
        }
        return false;
    }

    public void setContext(Context context)
    {
        this.baseContext = new WeakReference<>(context);
    }
}