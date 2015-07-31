package com.tradehero.th.network.service.ayondo;

import com.tradehero.th.network.ForLive;
import dagger.Module;
import dagger.Provides;
import retrofit.RestAdapter;

@Module(
        complete = false,
        library = true
)
public class RetrofitGameLiveAyondoProtectedModule
{
    //<editor-fold desc="API Services">
    @Provides LiveServiceAyondoRx provideLiveServiceAyondoRx (@ForLive RestAdapter adapter)
    {
        return adapter.create(LiveServiceAyondoRx .class);
    }
    //</editor-fold>
}
