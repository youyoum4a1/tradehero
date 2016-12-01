package com.androidth.general.network.service;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;

@Module(
        injects = {
        },
        complete = false,
        library = true
)
public class FlavorRetrofitProtectedModule
{
    //<editor-fold desc="API Services">
    @Provides @Singleton AlertPlanCheckServiceRx provideAlertPlanCheckServiceRx(Retrofit adapter)
    {
        return adapter.create(AlertPlanCheckServiceRx.class);
    }
    //</editor-fold>
}
