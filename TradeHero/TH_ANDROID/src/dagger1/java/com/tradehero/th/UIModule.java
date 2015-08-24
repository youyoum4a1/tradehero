package com.tradehero.th;

import com.tradehero.th.activities.ActivityModule;
import com.tradehero.th.auth.AuthenticationModule;
import com.tradehero.th.billing.BillingUIModule;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.FragmentModule;
import com.tradehero.th.fragments.settings.SettingsDrawerMenuItem;
import com.tradehero.th.fragments.settings.UpdateCenterDrawerMenuItem;
import com.tradehero.th.models.intent.IntentDaggerModule;
import com.tradehero.th.models.user.follow.SimpleFollowUserAssistant;
import com.tradehero.th.network.share.SocialNetworkUIModule;
import com.tradehero.th.ui.ViewWrapper;
import com.tradehero.th.widget.WidgetModule;
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
                SimpleFollowUserAssistant.class,
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
