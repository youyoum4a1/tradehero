package com.tradehero.th;

import com.tradehero.th.activities.ActivityModule;
import com.tradehero.th.auth.AuthenticationModule;
import com.tradehero.th.billing.BillingUIModule;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.DashboardResideMenu;
import com.tradehero.th.fragments.FragmentModule;
import com.tradehero.th.fragments.settings.SettingsResideMenuItem;
import com.tradehero.th.fragments.settings.SettingsResideMenuItemUpdateCenter;
import com.tradehero.th.models.intent.IntentDaggerModule;
import com.tradehero.th.models.user.follow.FollowUserAssistant;
import com.tradehero.th.models.user.follow.SimpleFollowUserAssistant;
import com.tradehero.th.network.share.SocialNetworkUIModule;
import com.tradehero.th.ui.UIComponents;
import com.tradehero.th.ui.ViewWrapper;
import com.tradehero.th.widget.WidgetModule;
import dagger.Module;
import dagger.Provides;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import javax.inject.Singleton;
import org.ocpsoft.prettytime.PrettyTime;

@Module(
        includes = {
                UIComponents.class,
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
                SimpleFollowUserAssistant.class,
                SettingsResideMenuItemUpdateCenter.class,
                SettingsResideMenuItem.class,
        },
        complete = false,
        library = true
)
public class UIModule
{
    @Provides PrettyTime providePrettyTime()
    {
        return new PrettyTime();
    }

    @Provides @Singleton ViewWrapper provideViewWrapper()
    {
        return ViewWrapper.DEFAULT;
    }

    @Provides(type = Provides.Type.SET_VALUES) @Singleton Set<DashboardNavigator.DashboardFragmentWatcher> provideDashboardNavigatorWatchers(
            DashboardResideMenu dashboardResideMenu
    )
    {
        return new HashSet<>(Arrays.<DashboardNavigator.DashboardFragmentWatcher>asList(dashboardResideMenu));
    }
}
