package com.androidth.general;

import com.tradehero.th.BuildTypeUIModule;
import com.androidth.general.activities.ActivityModule;
import com.androidth.general.auth.AuthenticationModule;
import com.androidth.general.billing.BillingUIModule;
import com.androidth.general.fragments.DashboardNavigator;
import com.androidth.general.fragments.FragmentModule;
import com.androidth.general.fragments.settings.SettingsDrawerMenuItem;
import com.androidth.general.fragments.settings.UpdateCenterDrawerMenuItem;
import com.androidth.general.models.intent.IntentDaggerModule;
import com.androidth.general.models.user.follow.FollowUserAssistant;
import com.androidth.general.network.share.SocialNetworkUIModule;
import com.androidth.general.ui.ViewWrapper;
import com.androidth.general.widget.WidgetModule;
import dagger.Module;
import dagger.Provides;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import javax.inject.Singleton;

@Module(
        includes = {
                FragmentModule.class,
                ActivityModule.class,
                WidgetModule.class,
                // Following modules need injected Activity to work, therefore, it cannot be in AppModule
                SocialNetworkUIModule.class,
                BillingUIModule.class,
                IntentDaggerModule.class,
                BuildTypeUIModule.class,
                AuthenticationModule.class
        },
        injects = {
                FollowUserAssistant.class,
                UpdateCenterDrawerMenuItem.class,
                SettingsDrawerMenuItem.class,
        },
        complete = false,
        library = true
)
public class UIModule
{
    @Provides @Singleton ViewWrapper provideViewWrapper()
    {
        return ViewWrapper.DEFAULT;
    }

    @Provides(type = Provides.Type.SET_VALUES) @Singleton Set<DashboardNavigator.DashboardFragmentWatcher> provideDashboardNavigatorWatchers(
    )
    {
        return new HashSet<>(Arrays.<DashboardNavigator.DashboardFragmentWatcher>asList());
    }
}
