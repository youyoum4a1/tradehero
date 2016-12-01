package com.androidth.general.network.service.ayondo;

import com.androidth.general.network.ForLive;
import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;

@Module(
        complete = false,
        library = true
)
public class RetrofitGameLiveAyondoProtectedModule
{
    //<editor-fold desc="API Services">
    @Provides LiveServiceAyondoRx provideLiveServiceAyondoRx (@ForLive Retrofit adapter)
    {
        return adapter.create(LiveServiceAyondoRx .class);
    }
    //</editor-fold>
}
