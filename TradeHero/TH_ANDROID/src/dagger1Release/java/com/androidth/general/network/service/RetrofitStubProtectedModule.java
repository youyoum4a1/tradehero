package com.androidth.general.network.service;

import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

import retrofit2.Retrofit;

@Module(
        overrides = true,
        complete = false,
        library = true
)
public class RetrofitStubProtectedModule
{
    @Provides @Singleton AchievementMockServiceRx provideAchievementMockServiceRx(Retrofit adapter)
    {
        return adapter.create(AchievementMockServiceRx.class);
    }

    @Deprecated // TODO remove when server ready
    @Provides @Singleton SecurityServiceWrapper provideSecurityServiceWrapper(SecurityServiceWrapperStub securityServiceWrapper)
    {
        return securityServiceWrapper;
    }

    @Deprecated // TODO remove when server ready
    @Provides @Singleton UserServiceWrapper provideUserServiceWrapper(UserServiceWrapperStub userServiceWrapper)
    {
        return userServiceWrapper;
    }
}
