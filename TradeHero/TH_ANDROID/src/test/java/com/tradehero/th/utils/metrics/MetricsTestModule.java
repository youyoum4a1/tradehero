package com.ayondo.academy.utils.metrics;

import com.tapstream.sdk.Api;
import com.tradehero.metrics.Analytics;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

import static org.mockito.Mockito.mock;

@Module(
        library = true,
        complete = false,
        overrides = true
)
public class MetricsTestModule
{
    @Provides @Singleton Api provideMockTapStream()
    {
        return mock(Api.class);
    }

    @Provides @Singleton Analytics provideMockLocalyticsSession()
    {
        return mock(Analytics.class);
    }
}
