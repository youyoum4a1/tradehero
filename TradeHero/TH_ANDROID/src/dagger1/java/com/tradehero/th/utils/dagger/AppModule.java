package com.ayondo.academy.utils.dagger;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import com.tradehero.FlavorModule;
import com.ayondo.academy.BuildTypeModule;
import com.ayondo.academy.api.ObjectMapperWrapper;
import com.ayondo.academy.api.discussion.MessageHeaderDTO;
import com.ayondo.academy.base.THApp;
import com.ayondo.academy.billing.BillingModule;
import com.ayondo.academy.filter.FilterModule;
import com.ayondo.academy.models.ModelsModule;
import com.ayondo.academy.models.chart.ChartModule;
import com.ayondo.academy.models.portfolio.DisplayablePortfolioFetchAssistant;
import com.ayondo.academy.models.push.PushModule;
import com.ayondo.academy.network.NetworkModule;
import com.ayondo.academy.network.share.SocialNetworkAppModule;
import com.ayondo.academy.persistence.PersistenceModule;
import com.ayondo.academy.persistence.prefs.LanguageCode;
import com.ayondo.academy.utils.metrics.MetricsModule;
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
