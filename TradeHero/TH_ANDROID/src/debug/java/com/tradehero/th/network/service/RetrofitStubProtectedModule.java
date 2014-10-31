package com.tradehero.th.network.service;

import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;
import retrofit.RestAdapter;

@Module(
        includes = {
        },
        injects = {
        },
        overrides = true,
        complete = false,
        library = true
)
public class RetrofitStubProtectedModule
{
    @Provides @Singleton AchievementMockServiceRx provideAchievementMockServiceRx(RestAdapter adapter)
    {
        return adapter.create(AchievementMockServiceRx.class);
    }
}
