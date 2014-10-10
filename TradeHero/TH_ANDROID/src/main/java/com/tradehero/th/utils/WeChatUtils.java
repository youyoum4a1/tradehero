package com.tradehero.th.utils;

import android.content.Context;
import com.tradehero.th.auth.wechat.WechatAuthenticationProvider;
import com.tradehero.th.base.THUser;
import com.tradehero.th.misc.callback.LogInCallback;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class WeChatUtils
{

    private final WechatAuthenticationProvider provider;

    @Inject
    public WeChatUtils(WechatAuthenticationProvider provider)
    {
        this.provider = provider;
        THUser.registerAuthenticationProvider(provider);
    }

    public void logIn(Context context, LogInCallback callback, String wechatCode)
    {
        provider.with(context);
        provider.setCode(wechatCode);
        THUser.logInWithAsync(provider.getAuthType(), callback);
    }
}
