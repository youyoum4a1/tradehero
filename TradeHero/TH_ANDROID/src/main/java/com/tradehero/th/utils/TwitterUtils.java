package com.tradehero.th.utils;

import android.app.Activity;
import com.tradehero.th.api.users.UserLoginDTO;
import com.tradehero.th.auth.TwitterAuthenticationProvider;
import com.tradehero.th.base.THUser;
import com.tradehero.th.misc.callback.LogInCallback;
import javax.inject.Inject;
import javax.inject.Singleton;
import rx.Observable;

@Singleton
public final class TwitterUtils implements SocialAuthUtils
{
    @Inject protected TwitterAuthenticationProvider provider;

    @Inject public TwitterUtils(TwitterAuthenticationProvider provider)
    {
        this.provider = provider;
        THUser.registerAuthenticationProvider(provider);
    }

    @Override public void logIn(Activity context, LogInCallback callback)
    {
        provider.with(context);
        THUser.logInWithAsync(provider.getAuthType(), callback);
    }
}