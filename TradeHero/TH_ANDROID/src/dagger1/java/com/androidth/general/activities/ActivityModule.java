package com.androidth.general.activities;

import com.androidth.general.common.activities.ActivityResultRequester;
import com.androidth.general.auth.FacebookAuthenticationProvider;
import com.androidth.general.auth.SocialAuth;
import com.androidth.general.auth.weibo.WeiboAuthenticationProvider;
import com.androidth.general.billing.THBillingInteractorRx;
import com.androidth.general.wxapi.WXEntryActivity;
import dagger.Module;
import dagger.Provides;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.inject.Singleton;

@Module(
        includes = {
                ActivityGameLiveModule.class,
        },
        injects = {
                AdminSettingsActivity.class,
                AlertManagerActivity.class,
                AuthenticationActivity.class,
                DashboardActivity.class,
                FacebookShareActivity.class,
                FriendsInvitationActivity.class,
                OnBoardActivity.class,
                PrivateDiscussionActivity.class,
                SettingsActivity.class,
                SettingsProfileActivity.class,
                SplashActivity.class,
                StoreScreenActivity.class,
                UpdateCenterActivity.class,
                WXEntryActivity.class,
        },
        complete = false,
        library = true
)
public class ActivityModule
{
    @Provides(type = Provides.Type.SET_VALUES) @Singleton
    Set<ActivityResultRequester> provideActivityResultRequesters(
            @SocialAuth Set<ActivityResultRequester> socialAuthActivityResultRequesters,
            THBillingInteractorRx thBillingInteractor) {

        Set<ActivityResultRequester> requests = new HashSet<>();
        requests.addAll(socialAuthActivityResultRequesters);
        requests.add(thBillingInteractor);
        return Collections.unmodifiableSet(requests);
    }

    @Provides(type = Provides.Type.SET_VALUES) @Singleton @SocialAuth
    Set<ActivityResultRequester> provideSocialAuthActivityResultRequesters(
            FacebookAuthenticationProvider facebookAuthenticationProvider,
            WeiboAuthenticationProvider weiboAuthenticationProvider) {

        Set<ActivityResultRequester> requests = new HashSet<>();
        requests.add(facebookAuthenticationProvider);
        requests.add(weiboAuthenticationProvider);
        return Collections.unmodifiableSet(requests);
    }
}
