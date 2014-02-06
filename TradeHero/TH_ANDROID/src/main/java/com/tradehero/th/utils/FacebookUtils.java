package com.tradehero.th.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import com.tradehero.th.auth.FacebookAuthenticationProvider;
import com.tradehero.th.base.Application;
import com.tradehero.th.base.THUser;
import com.tradehero.th.misc.callback.LogInCallback;
import java.util.Collection;
import java.util.Collections;
import javax.inject.Inject;
import javax.inject.Singleton;

/** Created with IntelliJ IDEA. User: tho Date: 8/15/13 Time: 1:07 PM Copyright (c) TradeHero */
@Singleton
public class FacebookUtils
{
    private final FacebookAuthenticationProvider provider;

    @Inject public FacebookUtils(FacebookAuthenticationProvider provider)
    {
        this.provider = provider;
        THUser.registerAuthenticationProvider(provider);
    }

    public void logIn(Activity activity, LogInCallback callback)
    {
        provider.setActivity(activity);
        THUser.logInWithAsync(provider.getAuthType(), callback);
    }

    public void finishAuthentication(int requestCode, int resultCode, Intent data)
    {
        if (provider != null)
        {
            provider.onActivityResult(requestCode, resultCode, data);
        }
    }
}
