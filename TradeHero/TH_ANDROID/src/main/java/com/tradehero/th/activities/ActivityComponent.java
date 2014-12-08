package com.tradehero.th.activities;

import com.tradehero.th.auth.FacebookAuthenticationProvider;
import com.tradehero.th.auth.SocialAuth;
import com.tradehero.th.auth.weibo.WeiboAuthenticationProvider;
import com.tradehero.th.billing.THBillingInteractor;
import dagger.Component;
import dagger.Provides;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.inject.Singleton;

@Component(modules = { ActivityComponent.Module.class })
public interface ActivityComponent
{
    @dagger.Module
    static class Module
    {
        @Provides(type = Provides.Type.SET_VALUES) @Singleton Set<ActivityResultRequester> provideActivityResultRequesters(
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
}
