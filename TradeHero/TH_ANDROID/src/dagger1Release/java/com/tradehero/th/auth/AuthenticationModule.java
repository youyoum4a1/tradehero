package com.ayondo.academy.auth;

import android.content.Context;
import com.facebook.TokenCachingStrategy;
import com.ayondo.academy.api.social.SocialNetworkEnum;
import com.ayondo.academy.auth.linkedin.LinkedInAuthenticationProvider;
import com.ayondo.academy.auth.tencent_qq.QQAuthenticationProvider;
import com.ayondo.academy.auth.weibo.WeiboAuthenticationProvider;
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
    @Provides @Singleton @SocialAuth Map<SocialNetworkEnum, AuthenticationProvider> provideSocialAuthTypeToSocialProviderMap(
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
