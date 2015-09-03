package com.tradehero.th.utils.dagger;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import com.tradehero.FlavorModule;
import com.tradehero.th.BuildTypeModule;
import com.tradehero.th.api.ObjectMapperWrapper;
import com.tradehero.th.api.discussion.MessageHeaderDTO;
import com.tradehero.th.base.THApp;
import com.tradehero.th.billing.BillingModule;
import com.tradehero.th.filter.FilterModule;
import com.tradehero.th.models.ModelsModule;
import com.tradehero.th.models.chart.ChartModule;
import com.tradehero.th.models.portfolio.DisplayablePortfolioFetchAssistant;
import com.tradehero.th.models.push.PushModule;
import com.tradehero.th.network.NetworkModule;
import com.tradehero.th.network.share.SocialNetworkAppModule;
import com.tradehero.th.persistence.PersistenceModule;
import com.tradehero.th.persistence.prefs.LanguageCode;
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
                PersistenceModule.class,
                ChartModule.class,
                FilterModule.class,
                BillingModule.class,
                SocialNetworkAppModule.class,
                PushModule.class,
                BuildTypeModule.class,
        },
        injects =
                {
                        THApp.class,
                        DisplayablePortfolioFetchAssistant.class,

                        MessageHeaderDTO.class,

                        ObjectMapperWrapper.class,
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

    @Provides @Singleton ConnectivityManager provideConnectivityManager(Context context) {

        return (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    @Provides
    NetworkInfo provideNetworkInfo(ConnectivityManager connectivityManager)
    {
        return connectivityManager.getActiveNetworkInfo();
    }
}
