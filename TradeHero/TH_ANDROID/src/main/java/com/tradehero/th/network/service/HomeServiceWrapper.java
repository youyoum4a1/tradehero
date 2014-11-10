package com.tradehero.th.network.service;

import android.support.annotation.NonNull;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.utils.Constants;
import javax.inject.Inject;
import retrofit.client.Response;
import rx.Observable;

public class HomeServiceWrapper
{
    @NonNull private final HomeServiceRx homeServiceRx;

    //<editor-fold desc="Constructors">
    @Inject public HomeServiceWrapper(
            @NonNull HomeServiceRx homeServiceRx)
    {
        this.homeServiceRx = homeServiceRx;
    }
    //</editor-fold>

    //<editor-fold desc="Get Home Page Content">
    @NonNull public Observable<Response> getHomePageContentRx(@NonNull UserBaseKey userBaseKey)
    {
        return homeServiceRx.getHomePageContent(
                userBaseKey.key,
                Constants.USE_BETA_HOME_PAGE ? true : null);
    }
    //</editor-fold>
}
