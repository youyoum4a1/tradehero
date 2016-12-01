package com.androidth.general.network.service;

import com.androidth.general.network.*;
import com.androidth.general.network.service.ayondo.RetrofitGameLiveAyondoProtectedModule;
import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;

@Module(
        includes = {
//                RetrofitGLiveProtectedBuildTypeModule.class,
                RetrofitGameLiveAyondoProtectedModule.class,
        },
        complete = false,
        library = true
)
public class RetrofitGameLiveProtectedModule
{
    //<editor-fold desc="API Services">
    @Provides LiveServiceRx provideLiveServiceRx (@ForLive Retrofit adapter)
    {
        return adapter.create(LiveServiceRx .class);
    }

    @Provides Live1BServiceRx provideLive1BServiceRx (@ForLive1B Retrofit adapter)
    {
        return adapter.create(Live1BServiceRx .class);
    }

    @Provides KycServicesRx provideKycServiceRx (@ForLive Retrofit adapter)
    {
        return adapter.create(KycServicesRx .class);
    }
    //</editor-fold>
}
