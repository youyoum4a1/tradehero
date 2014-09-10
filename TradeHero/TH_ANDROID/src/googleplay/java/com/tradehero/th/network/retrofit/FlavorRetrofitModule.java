package com.tradehero.th.network.retrofit;

import com.tradehero.th.network.service.AlertPlanCheckService;
import com.tradehero.th.network.service.FlavorRetrofitProtectedModule;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit.RestAdapter;

@Module(
        includes = {
                FlavorRetrofitProtectedModule.class,
        },
        injects = {
        },
        complete = false,
        library = true
)
public class FlavorRetrofitModule
{
    //<editor-fold desc="API Services">
    @Provides @Singleton AlertPlanCheckService provideAlertPlanCheckService(RestAdapter adapter)
    {
        return adapter.create(AlertPlanCheckService.class);
    }
    //</editor-fold>
}
