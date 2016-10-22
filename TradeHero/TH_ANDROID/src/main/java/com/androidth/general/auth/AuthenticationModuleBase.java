package com.androidth.general.auth;

//import com.facebook.SharedPreferencesTokenCachingStrategy;
//import com.facebook.TokenCachingStrategy;
import com.androidth.general.api.social.SocialNetworkEnum;
import com.androidth.general.auth.linkedin.LinkedInAuthenticationProvider;
import com.androidth.general.auth.weibo.WeiboAuthenticationProvider;
import dagger.Lazy;
import java.util.HashMap;
import java.util.Map;

public class AuthenticationModuleBase
{
    public static Map<SocialNetworkEnum, AuthenticationProvider> provideSocialAuthTypeToSocialProviderMap(
            Lazy<FacebookAuthenticationProvider> facebookAuthenticationProvider,
            Lazy<TwitterAuthenticationProvider> twitterAuthenticationProvider,
            Lazy<LinkedInAuthenticationProvider> linkedInAuthenticationProvider,
            Lazy<WeiboAuthenticationProvider> weiboAuthenticationProvider)
    {
        Map<SocialNetworkEnum, AuthenticationProvider> enumToUtilMap = new HashMap<>();
        enumToUtilMap.put(SocialNetworkEnum.FB, facebookAuthenticationProvider.get());
        enumToUtilMap.put(SocialNetworkEnum.TW, twitterAuthenticationProvider.get());
        enumToUtilMap.put(SocialNetworkEnum.LN, linkedInAuthenticationProvider.get());
        enumToUtilMap.put(SocialNetworkEnum.WB, weiboAuthenticationProvider.get());
        return enumToUtilMap;
    }

//    public static TokenCachingStrategy provideFacebookTokenCachingStrategy(Context context)
//    {
//        return new SharedPreferencesTokenCachingStrategy(context);
//    }
}
