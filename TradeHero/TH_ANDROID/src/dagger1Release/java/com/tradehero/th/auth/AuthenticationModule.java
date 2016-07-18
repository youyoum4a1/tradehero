package com.tradehero.th.auth;

import android.content.Context;

import com.androidth.general.auth.AuthenticationModuleBase;
import com.androidth.general.auth.AuthenticationProvider;
import com.androidth.general.auth.FacebookAuthenticationProvider;
import com.androidth.general.auth.SocialAuth;
import com.androidth.general.auth.TwitterAuthenticationProvider;
import com.facebook.TokenCachingStrategy;
import com.androidth.general.api.social.SocialNetworkEnum;
import com.androidth.general.auth.linkedin.LinkedInAuthenticationProvider;
import com.tradehero.th.auth.tencent_qq.QQAuthenticationProvider;
import com.androidth.general.auth.weibo.WeiboAuthenticationProvider;
import dagger.Lazy;
import dagger.Module;
import dagger.Provides;
import java.util.Collections;
import java.util.Map;
import javax.inject.Singleton;

@Module(
        library = true,
        complete = false
)
public class AuthenticationModule
{
    /** TODO waiting for dagger to have map injection feature, it would make following method nicer */
    @Provides @Singleton @SocialAuth
    Map<SocialNetworkEnum, AuthenticationProvider> provideSocialAuthTypeToSocialProviderMap(
            Lazy<FacebookAuthenticationProvider> facebookAuthenticationProvider,
            Lazy<TwitterAuthenticationProvider> twitterAuthenticationProvider,
            Lazy<LinkedInAuthenticationProvider> linkedInAuthenticationProvider,
            Lazy<WeiboAuthenticationProvider> weiboAuthenticationProvider,
            Lazy<QQAuthenticationProvider> qqAuthenticationProvider
    )
    {
        Map<SocialNetworkEnum, AuthenticationProvider> enumToUtilMap = AuthenticationModuleBase.provideSocialAuthTypeToSocialProviderMap(
                facebookAuthenticationProvider,
                twitterAuthenticationProvider,
                linkedInAuthenticationProvider,
                weiboAuthenticationProvider);
        enumToUtilMap.put(SocialNetworkEnum.QQ, qqAuthenticationProvider.get());
        return Collections.unmodifiableMap(enumToUtilMap);
    }

    @Provides @Singleton TokenCachingStrategy provideFacebookTokenCachingStrategy(Context context)
    {
        return AuthenticationModuleBase.provideFacebookTokenCachingStrategy(context);
    }
}
