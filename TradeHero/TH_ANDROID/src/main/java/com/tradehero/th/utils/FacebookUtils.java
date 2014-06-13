package com.tradehero.th.utils;

import android.app.Activity;
import android.content.Intent;
import com.tradehero.th.auth.FacebookAuthenticationProvider;
import com.tradehero.th.base.THUser;
import com.tradehero.th.misc.callback.LogInCallback;
import javax.inject.Inject;
import javax.inject.Singleton;

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
