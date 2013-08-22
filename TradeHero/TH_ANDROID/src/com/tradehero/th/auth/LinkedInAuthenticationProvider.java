package com.tradehero.th.auth;

import android.content.Context;
import com.tradehero.th.auth.linkedin.LinkedIn;
import java.lang.ref.WeakReference;
import org.json.JSONException;
import org.json.JSONObject;

/** Created with IntelliJ IDEA. User: tho Date: 8/21/13 Time: 12:49 PM Copyright (c) TradeHero */
public class LinkedInAuthenticationProvider
        implements THAuthenticationProvider
{
    private final LinkedIn linkedIn;
    private WeakReference<Context> baseContext;
    private THAuthenticationCallback currentOperationCallback;

    public LinkedInAuthenticationProvider(LinkedIn linkedIn)
    {
        this.linkedIn = linkedIn;
    }

    public void setContext(Context context)
    {
        this.baseContext = new WeakReference<>(context);
    }

    @Override public void authenticate(final THAuthenticationCallback callback)
    {
        if (this.currentOperationCallback != null)
        {
            cancel();
        }
        this.currentOperationCallback = callback;

        Context context = this.baseContext == null ? null : this.baseContext.get();
        if (context == null)
        {
            throw new IllegalStateException(
                    "Context must be non-null for Twitter authentication to proceed.");
        }

        this.linkedIn.authorize(context, new THAuthenticationCallback()
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
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override public boolean restoreAuthentication(JSONObject paramJSONObject)
    {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override public void cancel()
    {
        handleCancel(this.currentOperationCallback);
    }

    public String getAuthType()
    {
        return "linkedin";
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

    public JSONObject getAuthData(String authToken, String authTokenSecret)
            throws JSONException
    {
        JSONObject authData = new JSONObject();
        authData.put("type", "linkedin");
        authData.put("auth_token", authToken);
        authData.put("auth_token_secret", authTokenSecret);
        authData.put("consumer_key", this.linkedIn.getConsumerKey());
        authData.put("consumer_secret", this.linkedIn.getConsumerSecret());
        return authData;
    }
}
