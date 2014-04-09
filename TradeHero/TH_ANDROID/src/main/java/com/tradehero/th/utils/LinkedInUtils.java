package com.tradehero.th.utils;

import android.content.Context;
import com.tradehero.common.persistence.DTOKey;
import com.tradehero.th.auth.LinkedInAuthenticationProvider;
import com.tradehero.th.base.THUser;
import com.tradehero.th.misc.callback.LogInCallback;
import javax.inject.Inject;
import javax.inject.Singleton;

/** Created with IntelliJ IDEA. User: tho Date: 8/15/13 Time: 1:14 PM Copyright (c) TradeHero */
@Singleton
public class LinkedInUtils implements SocialSharer
{
    private final LinkedInAuthenticationProvider provider;

    @Inject public LinkedInUtils(LinkedInAuthenticationProvider provider)
    {
        this.provider = provider;
        THUser.registerAuthenticationProvider(provider);
    }

    public void logIn(Context context, LogInCallback callback)
    {
        provider.with(context);
        THUser.logInWithAsync(provider.getAuthType(), callback);
    }

    @Override public void share(Context context, DTOKey shareDtoKey)
    {
        // TODO
    }
}
