package com.androidth.general.models.intent;

import com.androidth.general.models.intent.competition.ProviderIntentFactory;
import com.androidth.general.models.intent.security.SecurityIntentFactory;
import dagger.Module;
import dagger.Provides;
import java.util.Set;
import javax.inject.Singleton;

@Module(
        complete = false,
        library = true
)
public class IntentDaggerModule
{
    public IntentDaggerModule()
    {
    }

    @Provides(type = Provides.Type.SET)
    THIntentFactory provideProviderIntentFactory(ProviderIntentFactory factory)
    {
        return factory;
    }
    @Provides(type = Provides.Type.SET)
    THIntentFactory provideSecurityIntentFactory(SecurityIntentFactory factory)
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
