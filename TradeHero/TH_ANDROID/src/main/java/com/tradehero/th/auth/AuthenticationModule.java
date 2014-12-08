package com.tradehero.th.auth;

import android.content.Context;
import com.facebook.SharedPreferencesTokenCachingStrategy;
import com.facebook.TokenCachingStrategy;
import com.tradehero.th.api.social.SocialNetworkEnum;
import com.tradehero.th.auth.linkedin.LinkedInAuthenticationProvider;
import com.tradehero.th.auth.tencent_qq.QQAuthenticationProvider;
import com.tradehero.th.auth.weibo.WeiboAuthenticationProvider;
import com.tradehero.th.utils.Constants;
import dagger.Lazy;
import dagger.Module;
import dagger.Provides;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Singleton;

@Module
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
        Map<SocialNetworkEnum, AuthenticationProvider> enumToUtilMap = new HashMap<>();
        enumToUtilMap.put(SocialNetworkEnum.FB, facebookAuthenticationProvider.get());
        enumToUtilMap.put(SocialNetworkEnum.TW, twitterAuthenticationProvider.get());
        enumToUtilMap.put(SocialNetworkEnum.LN, linkedInAuthenticationProvider.get());
        enumToUtilMap.put(SocialNetworkEnum.WB, weiboAuthenticationProvider.get());
        if (Constants.RELEASE)
        {
            enumToUtilMap.put(SocialNetworkEnum.QQ, qqAuthenticationProvider.get());
        }
        return Collections.unmodifiableMap(enumToUtilMap);
    }

    @Provides @Singleton TokenCachingStrategy provideFacebookTokenCachingStrategy(Context context)
    {
        return new SharedPreferencesTokenCachingStrategy(context);
    }
}
