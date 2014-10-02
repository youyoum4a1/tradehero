package com.tradehero.th.auth;

import com.tradehero.th.api.social.SocialNetworkEnum;
import com.tradehero.th.auth.linkedin.LinkedInAuthenticationProvider;
import com.tradehero.th.auth.weibo.WeiboAuthenticationProvider;
import dagger.Module;
import dagger.Provides;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Singleton;

@Module(
        library = true,
        complete = false
)
public class AuthenticationModule
{
    @Provides @Singleton @SocialAuth Map<SocialNetworkEnum, AuthenticationProvider> provideSocialAuthTypeMap(
            FacebookAuthenticationProvider facebookUtils,
            TwitterAuthenticationProvider twitterUtils,
            LinkedInAuthenticationProvider linkedInUtils,
            WeiboAuthenticationProvider weiboUtils
    )
    {
        Map<SocialNetworkEnum, AuthenticationProvider> enumToUtilMap = new HashMap<>();
        enumToUtilMap.put(SocialNetworkEnum.FB, facebookUtils);
        enumToUtilMap.put(SocialNetworkEnum.TW, twitterUtils);
        enumToUtilMap.put(SocialNetworkEnum.LN, linkedInUtils);
        enumToUtilMap.put(SocialNetworkEnum.WB, weiboUtils);
        return Collections.unmodifiableMap(enumToUtilMap);
    }
}
