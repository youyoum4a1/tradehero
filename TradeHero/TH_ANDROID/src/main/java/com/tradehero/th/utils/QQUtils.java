package com.tradehero.th.utils;

import android.content.Context;
import com.tradehero.th.auth.tencent_qq.QQAuthenticationProvider;
import com.tradehero.th.base.THUser;
import com.tradehero.th.misc.callback.LogInCallback;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class QQUtils
{

    private final QQAuthenticationProvider provider;

    @Inject
    public QQUtils(QQAuthenticationProvider provider)
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
