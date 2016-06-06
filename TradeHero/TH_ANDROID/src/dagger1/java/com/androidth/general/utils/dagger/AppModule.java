package com.androidth.general.utils.dagger;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.androidth.general.FlavorModule;
import com.androidth.general.api.ObjectMapperWrapper;
import com.androidth.general.api.discussion.MessageHeaderDTO;
import com.androidth.general.base.THApp;
import com.androidth.general.billing.BillingModule;
import com.androidth.general.filter.FilterModule;
import com.androidth.general.models.ModelsModule;
import com.androidth.general.models.chart.ChartModule;
import com.androidth.general.models.portfolio.DisplayablePortfolioFetchAssistant;
import com.androidth.general.models.push.PushModule;
import com.androidth.general.network.NetworkModule;
import com.androidth.general.network.share.SocialNetworkAppModule;
import com.androidth.general.persistence.PersistenceModule;
import com.androidth.general.persistence.prefs.LanguageCode;
import com.androidth.general.utils.metrics.MetricsModule;
import com.tradehero.th.BuildTypeModule;

import java.util.Locale;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

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
