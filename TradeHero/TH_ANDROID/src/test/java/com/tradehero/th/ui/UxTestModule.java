package com.tradehero.th.ui;

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
public class UxTestModule
{
    @Provides @Singleton Api provideMockTapStream()
    {
        return mock(Api.class);
    }

    @Provides @Singleton LocalyticsSession provideMockLocalyticsSession()
    {
        return mock(LocalyticsSession.class);
    }
}
