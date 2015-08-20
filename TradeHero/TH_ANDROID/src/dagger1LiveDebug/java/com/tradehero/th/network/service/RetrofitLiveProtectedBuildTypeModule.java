package com.tradehero.th.network.service;

import dagger.Module;
import dagger.Provides;

@Module(
        complete = false,
        library = true,
        overrides = true
)
public class RetrofitLiveProtectedBuildTypeModule
{
    @Provides LiveServiceWrapper provideLiveServiceWrapper(DummyAyondoLiveServiceWrapper liveServiceWrapper)
    {
        return liveServiceWrapper;
    }
}
