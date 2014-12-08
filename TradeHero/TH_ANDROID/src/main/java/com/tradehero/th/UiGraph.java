package com.tradehero.th;

import com.special.residemenu.ResideMenu;
import com.tradehero.th.activities.ActivityComponent;
import com.tradehero.th.auth.AuthenticationModule;
import com.tradehero.th.billing.BillingUIModule;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.DashboardResideMenu;
import com.tradehero.th.fragments.FragmentComponent;
import com.tradehero.th.fragments.settings.SettingsResideMenuItem;
import com.tradehero.th.models.intent.IntentDaggerModule;
import com.tradehero.th.models.user.follow.FollowUserAssistant;
import com.tradehero.th.models.user.follow.SimpleFollowUserAssistant;
import com.tradehero.th.network.share.SocialNetworkUIComponent;
import com.tradehero.th.ui.AppContainer;
import com.tradehero.th.ui.AppContainerImpl;
import com.tradehero.th.ui.ViewWrapper;
import com.tradehero.th.widget.WidgetComponent;
import dagger.Component;
import dagger.Provides;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import javax.inject.Singleton;
import org.ocpsoft.prettytime.PrettyTime;

@Component(modules = { UiGraph.Module.class, BillingUIModule.class, IntentDaggerModule.class, AuthenticationModule.class,
        BuildTypeUIModule.class })
public interface UiGraph extends
        FragmentComponent,
        ActivityComponent,
        WidgetComponent,
        SocialNetworkUIComponent

{
    void injectFollowUserAssistant(FollowUserAssistant target);
    void injectSimpleFollowUserAssistant(SimpleFollowUserAssistant target);
    void injectSettingsResideMenuItem(SettingsResideMenuItem target);
    // FIXME dagger void injectDebugFragmentModule.class,

    @dagger.Module
    static class Module
    {
        @Provides @Singleton AppContainer provideAppContainer(AppContainerImpl appContainer)
        {
            return appContainer;
        }

        @Provides @Singleton ResideMenu provideResideMenu(DashboardResideMenu dashboardResideMenu)
        {
            return dashboardResideMenu;
        }

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
}
