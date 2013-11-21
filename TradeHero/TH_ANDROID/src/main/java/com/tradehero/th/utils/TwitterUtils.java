package com.tradehero.th.utils;

import android.content.Context;
import com.tradehero.th.auth.TwitterAuthenticationProvider;
import com.tradehero.th.auth.operator.Twitter;
import com.tradehero.th.base.THUser;
import com.tradehero.th.misc.callback.LogInCallback;

/** Created with IntelliJ IDEA. User: tho Date: 8/15/13 Time: 1:13 PM Copyright (c) TradeHero */

public final class TwitterUtils
{
    private Twitter twitter;
    private TwitterAuthenticationProvider provider;

    private TwitterAuthenticationProvider getAuthenticationProvider()
    {
        if (provider == null)
        {
            provider = new TwitterAuthenticationProvider(getTwitter());
        }
        return provider;
    }

    public Twitter getTwitter()
    {
        if (twitter == null)
        {
            twitter = new Twitter("", "");
        }
        return twitter;
    }

    public TwitterUtils(String consumerKey, String consumerSecret)
    {
        getTwitter().setConsumerKey(consumerKey);
        getTwitter().setConsumerSecret(consumerSecret);
        THUser.registerAuthenticationProvider(getAuthenticationProvider());
    }

    public void logIn(Context context, LogInCallback callback)
    {
        provider.setContext(context);
        THUser.logInWithAsync(provider.getAuthType(), callback);
    }
}