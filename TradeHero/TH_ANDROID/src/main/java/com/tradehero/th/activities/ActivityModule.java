package com.tradehero.th.activities;

import com.tradehero.th.auth.FacebookAuthenticationProvider;
import com.tradehero.th.auth.SocialAuth;
import com.tradehero.th.auth.weibo.WeiboAuthenticationProvider;
import com.tradehero.th.billing.THBillingInteractor;
import com.tradehero.th.wxapi.WXEntryActivity;
import dagger.Module;
import dagger.Provides;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.inject.Singleton;

@Module(
        injects = {
                SplashActivity.class,
                DashboardActivity.class,
                AuthenticationActivity.class,
                WXEntryActivity.class
        },
        staticInjections = {
        },
        complete = false,
        library = true
)
public class ActivityModule
{
    @Provides(type = Provides.Type.SET_VALUES) @Singleton
    Set<ActivityResultRequester> provideActivityResultRequesters(
            @SocialAuth Set<ActivityResultRequester> socialAuthActivityResultRequesters,
            THBillingInteractor thBillingInteractor
    )
    {
        Set<ActivityResultRequester> requests = new HashSet<>();
        requests.addAll(socialAuthActivityResultRequesters);
        requests.add(thBillingInteractor);
        return Collections.unmodifiableSet(requests);
    }
    @Provides(type = Provides.Type.SET_VALUES) @Singleton @SocialAuth
    Set<ActivityResultRequester> provideSocialAuthActivityResultRequesters(
            FacebookAuthenticationProvider facebookAuthenticationProvider,
            WeiboAuthenticationProvider weiboAuthenticationProvider
    )
    {
        Set<ActivityResultRequester> requests = new HashSet<>();
        requests.add(facebookAuthenticationProvider);
        requests.add(weiboAuthenticationProvider);
        return Collections.unmodifiableSet(requests);
    }
}
