package com.tradehero.th.utils;

import android.content.Context;
import com.tradehero.th.auth.LinkedInAuthenticationProvider;
import com.tradehero.th.auth.operator.LinkedIn;
import com.tradehero.th.base.THUser;
import com.tradehero.th.misc.callback.LogInCallback;

/** Created with IntelliJ IDEA. User: tho Date: 8/15/13 Time: 1:14 PM Copyright (c) TradeHero */
public class LinkedInUtils
{

    private static LinkedIn linkedIn;
    private static LinkedInAuthenticationProvider provider;
    private static boolean isInitialized;

    private static LinkedInAuthenticationProvider getAuthenticationProvider()
    {
        if (provider == null)
        {
            provider = new LinkedInAuthenticationProvider(getLinkedIn());
        }
        return provider;
    }

    public static LinkedIn getLinkedIn()
    {
        if (linkedIn == null)
        {
            linkedIn = new LinkedIn("", "");
        }
        return linkedIn;
    }

    public static void initialize(String consumerKey, String consumerSecret)
    {
        getLinkedIn().setConsumerKey(consumerKey);
        getLinkedIn().setConsumerSecret(consumerSecret);
        THUser.registerAuthenticationProvider(getAuthenticationProvider());
        isInitialized = true;
    }

    private static void checkInitialization()
    {
        if (!isInitialized)
        {
            throw new IllegalStateException(
                    "You must call LinkedInUtils.initialize() before using LinkedInUtils");
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
