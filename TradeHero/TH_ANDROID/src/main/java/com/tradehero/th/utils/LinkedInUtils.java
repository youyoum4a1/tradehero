package com.tradehero.th.utils;

import android.content.Context;
import com.tradehero.th.auth.LinkedInAuthenticationProvider;
import com.tradehero.th.auth.operator.LinkedIn;
import com.tradehero.th.base.THUser;
import com.tradehero.th.misc.callback.LogInCallback;

/** Created with IntelliJ IDEA. User: tho Date: 8/15/13 Time: 1:14 PM Copyright (c) TradeHero */
public class LinkedInUtils
{

    private LinkedIn linkedIn;
    private LinkedInAuthenticationProvider provider;

    private LinkedInAuthenticationProvider getAuthenticationProvider()
    {
        if (provider == null)
        {
            provider = new LinkedInAuthenticationProvider(getLinkedIn());
        }
        return provider;
    }

    public LinkedIn getLinkedIn()
    {
        if (linkedIn == null)
        {
            linkedIn = new LinkedIn("", "");
        }
        return linkedIn;
    }

    public LinkedInUtils(String consumerKey, String consumerSecret)
    {
        getLinkedIn().setConsumerKey(consumerKey);
        getLinkedIn().setConsumerSecret(consumerSecret);
        THUser.registerAuthenticationProvider(getAuthenticationProvider());
    }

    public void logIn(Context context, LogInCallback callback)
    {
        provider.setContext(context);
        THUser.logInWithAsync(provider.getAuthType(), callback);
    }
}
