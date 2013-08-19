package com.tradehero.th.utils;

import android.content.Context;
import com.tradehero.th.auth.TwitterAuthenticationProvider;
import com.tradehero.th.auth.twitter.Twitter;
import com.tradehero.th.base.THUser;
import com.tradehero.th.misc.callback.LogInCallback;

/** Created with IntelliJ IDEA. User: tho Date: 8/15/13 Time: 1:13 PM Copyright (c) TradeHero */

public final class TwitterUtils
{
    private static Twitter twitter;
    private static TwitterAuthenticationProvider provider;
    private static boolean isInitialized;

    private static TwitterAuthenticationProvider getAuthenticationProvider()
    {
        if (provider == null)
        {
            provider = new TwitterAuthenticationProvider(getTwitter());
        }
        return provider;
    }

    public static Twitter getTwitter()
    {
        if (twitter == null)
        {
            twitter = new Twitter("", "");
        }
        return twitter;
    }

    public static void initialize(String consumerKey, String consumerSecret)
    {
        getTwitter().setConsumerKey(consumerKey);
        getTwitter().setConsumerSecret(consumerSecret);
        THUser.registerAuthenticationProvider(getAuthenticationProvider());
        isInitialized = true;
    }

    private static void checkInitialization()
    {
        if (!isInitialized)
        {
            throw new IllegalStateException(
                    "You must call TwitterUtils.initialize() before using TwitterUtils");
        }
    }

    public static void logIn(String twitterId, String screenName, String authToken,
            String authTokenSecret, LogInCallback callback)
    {
        checkInitialization();
    }

    public static void logIn(Context context, LogInCallback callback)
    {
        checkInitialization();
        provider.setContext(context);
        THUser.logInWithAsync(provider.getAuthType(), callback);
    }
}