package com.tradehero.th.utils.dagger;

import android.app.Activity;
import android.content.Context;
import com.tradehero.FlavorModule;
import com.tradehero.th.activities.GuideActivity;
import com.tradehero.th.api.discussion.MessageHeaderDTO;
import com.tradehero.th.base.THApp;
import com.tradehero.th.base.THUser;
import com.tradehero.th.billing.BillingModule;
import com.tradehero.th.filter.FilterModule;
import com.tradehero.th.loaders.FriendListLoader;
import com.tradehero.th.loaders.TimelineListLoader;
import com.tradehero.th.loaders.security.SecurityListPagedLoader;
import com.tradehero.th.loaders.security.macquarie.MacquarieSecurityListPagedLoader;
import com.tradehero.th.models.ModelsModule;
import com.tradehero.th.models.chart.ChartModule;
import com.tradehero.th.models.portfolio.DisplayablePortfolioFetchAssistant;
import com.tradehero.th.models.push.PushModule;
import com.tradehero.th.models.user.follow.ChoiceFollowUserAssistantWithDialog;
import com.tradehero.th.network.NetworkModule;
import com.tradehero.th.persistence.portfolio.PortfolioCompactListRetrievedMilestone;
import com.tradehero.th.persistence.prefs.LanguageCode;
import com.tradehero.th.persistence.prefs.PreferenceModule;
import com.tradehero.th.persistence.user.UserProfileRetrievedMilestone;
import com.tradehero.th.persistence.watchlist.UserWatchlistPositionCache;
import com.tradehero.th.utils.metrics.MetricsModule;
import dagger.Module;
import dagger.Provides;
import java.util.Locale;
import javax.inject.Singleton;

@Module(
        includes = {
                FlavorModule.class,
                CacheModule.class,
                GraphicModule.class,
                NetworkModule.class,
                MetricsModule.class,
                ModelsModule.class,
                UserModule.class,
                PreferenceModule.class,
                ChartModule.class,
                FilterModule.class,
                BillingModule.class,
                PushModule.class
        },
        injects =
                {
                        THApp.class,
                        ChoiceFollowUserAssistantWithDialog.class,
                        SecurityListPagedLoader.class,
                        MacquarieSecurityListPagedLoader.class,

                        DisplayablePortfolioFetchAssistant.class,

                        TimelineListLoader.class,

                        PortfolioCompactListRetrievedMilestone.class,
                        UserProfileRetrievedMilestone.class,

                        UserWatchlistPositionCache.class,

                        FriendListLoader.class,
                        MessageHeaderDTO.class,
                        GuideActivity.class,
                },
        staticInjections =
                {
                        THUser.class,
                },
        complete = false,
        library = true // TODO remove this line
)
public class AppModule
{
    private final THApp THApp;

    public AppModule(THApp THApp)
    {
        this.THApp = THApp;
    }

    @Provides Context provideContext()
    {
        return THApp;
    }

    @Provides Activity provideActivity()
    {
        // Necessary to avoid injection error in Billing elements.
        throw new IllegalStateException("You should have another module that provides Activity");
    }

    @Provides Locale provideLocale(Context context)
    {
        return context.getResources().getConfiguration().locale;
    }

    @Provides @LanguageCode String provideCurrentLanguageCode(Locale locale)
    {
        return String.format("%s-%s", locale.getLanguage(), locale.getCountry());
    }

    @Provides @Singleton THApp provideApplication()
    {
        return THApp;
    }
}
