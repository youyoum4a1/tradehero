package com.tradehero.th.utils;

import android.app.Activity;
import android.content.Intent;
import com.tradehero.th.api.users.UserLoginDTO;
import com.tradehero.th.auth.weibo.WeiboAuthenticationProvider;
import com.tradehero.th.base.THUser;
import com.tradehero.th.misc.callback.LogInCallback;
import javax.inject.Inject;
import javax.inject.Singleton;
import rx.Observable;

@Singleton
public class WeiboUtils implements SocialAuthUtils
{
    private final WeiboAuthenticationProvider provider;

    @Inject
    public WeiboUtils(WeiboAuthenticationProvider provider)
    {
        this.provider = provider;
        THUser.registerAuthenticationProvider(provider);
    }

    @Override public void logIn(Activity context, LogInCallback callback)
    {
        provider.with(context);
        THUser.logInWithAsync(provider.getAuthType(), callback);
    }

    public void authorizeCallBack(int requestCode, int resultCode, Intent data)
    {
        provider.authorizeCallBack(requestCode, resultCode, data);
    }
}
