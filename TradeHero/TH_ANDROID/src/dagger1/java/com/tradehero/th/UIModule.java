package com.ayondo.academy;

import com.ayondo.academy.activities.ActivityModule;
import com.ayondo.academy.auth.AuthenticationModule;
import com.ayondo.academy.billing.BillingUIModule;
import com.ayondo.academy.fragments.DashboardNavigator;
import com.ayondo.academy.fragments.FragmentModule;
import com.ayondo.academy.fragments.settings.SettingsDrawerMenuItem;
import com.ayondo.academy.fragments.settings.UpdateCenterDrawerMenuItem;
import com.ayondo.academy.models.intent.IntentDaggerModule;
import com.ayondo.academy.models.user.follow.FollowUserAssistant;
import com.ayondo.academy.network.share.SocialNetworkUIModule;
import com.ayondo.academy.ui.ViewWrapper;
import com.ayondo.academy.widget.WidgetModule;
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
