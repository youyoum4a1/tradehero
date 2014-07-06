package com.tradehero.th.models.intent;

import com.tradehero.th.models.intent.competition.ProviderIntentFactory;
import com.tradehero.th.models.intent.interactor.ResetPortfolioIntentFactory;
import com.tradehero.th.models.intent.portfolio.PortfolioIntentFactory;
import com.tradehero.th.models.intent.security.SecurityIntentFactory;
import com.tradehero.th.models.intent.trending.TrendingIntentFactory;
import dagger.Module;
import dagger.Provides;
import java.util.Set;
import javax.inject.Singleton;

@Module(
        staticInjections =
                {
                        com.tradehero.th.models.intent.THIntent.class,
                },
        injects =
                {
                        com.tradehero.th.activities.DashboardActivity.class,
                        com.tradehero.th.fragments.web.THWebViewClient.class,
                },
        complete = false,
        library = true
)
public class IntentDaggerModule
{
    public IntentDaggerModule()
    {
    }

    @Provides(type = Provides.Type.SET)
    THIntentFactory provideTrendingIntentFactory(TrendingIntentFactory factory)
    {
        return factory;
    }
    @Provides(type = Provides.Type.SET)
    THIntentFactory provideTrendingIntentFactory(PortfolioIntentFactory factory)
    {
        return factory;
    }
    @Provides(type = Provides.Type.SET)
    THIntentFactory provideTrendingIntentFactory(ProviderIntentFactory factory)
    {
        return factory;
    }
    @Provides(type = Provides.Type.SET)
    THIntentFactory provideTrendingIntentFactory(SecurityIntentFactory factory)
    {
        return factory;
    }
    @Provides(type = Provides.Type.SET)
    THIntentFactory provideInteractorIntentFactory(ResetPortfolioIntentFactory factory)
    {
        return factory;
    }

    @Provides @Singleton THIntentFactory provideTHIntentFactory(THIntentFactoryImpl factory, Set<THIntentFactory> subFactories)
    {
        for (THIntentFactory subFactory: subFactories)
        {
            if (subFactory != null)
            {
                factory.addSubFactory(subFactory);
            }
        }
        return factory;
    }
}
