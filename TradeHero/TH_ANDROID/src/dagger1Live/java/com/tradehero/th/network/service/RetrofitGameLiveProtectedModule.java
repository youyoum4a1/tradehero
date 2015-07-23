package com.tradehero.th.network.service;

import com.tradehero.th.network.ForLive;
import dagger.Module;
import dagger.Provides;
import retrofit.RestAdapter;

@Module(
        includes = {
                RetrofitLiveProtectedBuildTypeModule.class,
        },
        complete = false,
        library = true
)
public class RetrofitGameLiveProtectedModule
{
    //<editor-fold desc="API Services">
    @Provides LiveServiceRx provideLiveServiceRx (@ForLive RestAdapter adapter)
    {
        return adapter.create(LiveServiceRx .class);
    }
    //</editor-fold>
}