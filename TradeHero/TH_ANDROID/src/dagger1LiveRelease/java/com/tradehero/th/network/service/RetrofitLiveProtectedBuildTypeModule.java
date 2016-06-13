package com.androidth.general.network.service;

import dagger.Module;

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
