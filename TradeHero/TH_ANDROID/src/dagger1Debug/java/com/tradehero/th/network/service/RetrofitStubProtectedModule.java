package com.ayondo.academy.network.service;

import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;
import retrofit.RestAdapter;

@Module(
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
