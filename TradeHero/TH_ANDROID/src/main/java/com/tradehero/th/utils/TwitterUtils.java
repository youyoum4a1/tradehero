package com.tradehero.th.utils;

import android.content.Context;
import com.tradehero.th.auth.TwitterAuthenticationProvider;
import com.tradehero.th.base.THUser;
import com.tradehero.th.misc.callback.LogInCallback;
import javax.inject.Inject;
import javax.inject.Singleton;


@Singleton
public final class TwitterUtils
{
    @Inject protected TwitterAuthenticationProvider provider;

    @Inject public TwitterUtils(TwitterAuthenticationProvider provider)
    {
        this.provider = provider;
        THUser.registerAuthenticationProvider(provider);
    }

    public void logIn(Context context, LogInCallback callback)
    {
        provider.with(context);
        THUser.logInWithAsync(provider.getAuthType(), callback);
    }
}