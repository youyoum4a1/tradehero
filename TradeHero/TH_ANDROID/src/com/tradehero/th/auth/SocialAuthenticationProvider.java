package com.tradehero.th.auth;

import android.content.Context;
import java.lang.ref.WeakReference;

/** Created with IntelliJ IDEA. User: tho Date: 8/22/13 Time: 6:06 PM Copyright (c) TradeHero */
public abstract class SocialAuthenticationProvider implements THAuthenticationProvider
{
    protected static final String SCREEN_NAME_KEY = "screen_name";
    protected static final String ID_KEY = "id";
    public static final String AUTH_TOKEN_SECRET_KEY = "auth_token_secret";
    public static final String AUTH_TOKEN_KEY = "auth_token";
    public static final String CONSUMER_KEY_KEY = "consumer_key";
    public static final String CONSUMER_SECRET_KEY = "consumer_secret";

    public static final String FACEBOOK_AUTH_TYPE = "TH-Facebook";
    public static final String TWITTER_AUTH_TYPE = "TH-Twitter";
    public static final String LINKEDIN_AUTH_TYPE = "TH-LinkedIn";

    protected WeakReference<Context> baseContext;
    protected THAuthenticationProvider.THAuthenticationCallback currentOperationCallback;

    public void setContext(Context context)
    {
        baseContext = new WeakReference<>(context);
    }

    @Override public void cancel()
    {
        handleCancel(this.currentOperationCallback);
    }

    protected void handleCancel(THAuthenticationProvider.THAuthenticationCallback callback)
    {
        if ((currentOperationCallback != callback) || (callback == null))
        {
            return;
        }
        try
        {
            callback.onCancel();
        }
        finally
        {
            currentOperationCallback = null;
        }
    }
}
