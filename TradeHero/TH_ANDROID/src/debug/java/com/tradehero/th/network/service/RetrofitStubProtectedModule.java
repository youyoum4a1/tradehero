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
    @Provides @Singleton AchievementMockServiceAsync provideAchievementMockServiceAsync(RestAdapter adapter)
    {
        return adapter.create(AchievementMockServiceAsync.class);
    }
}
