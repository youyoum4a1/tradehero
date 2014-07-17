package com.tradehero.th.utils.metrics;

import com.localytics.android.LocalyticsSession;
import com.tapstream.sdk.Api;
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
