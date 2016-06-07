package com.androidth.general.network.service;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit.RestAdapter;

@Module(
        injects = {
        },
        complete = false,
        library = true
)
public class FlavorRetrofitProtectedModule
{
    //<editor-fold desc="API Services">
    @Provides @Singleton AlertPlanCheckServiceRx provideAlertPlanCheckServiceRx(RestAdapter adapter)
    {
        return adapter.create(AlertPlanCheckServiceRx.class);
    }
    //</editor-fold>
}