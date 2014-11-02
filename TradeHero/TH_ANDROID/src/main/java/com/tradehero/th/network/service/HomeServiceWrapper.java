package com.tradehero.th.network.service;

import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.utils.Constants;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import retrofit.client.Response;
import rx.Observable;

public class HomeServiceWrapper
{
    @NotNull private final HomeService homeService;
    @NotNull private final HomeServiceRx homeServiceRx;

    //<editor-fold desc="Constructors">
    @Inject public HomeServiceWrapper(
            @NotNull HomeService homeService,
            @NotNull HomeServiceRx homeServiceRx)
    {
        this.homeService = homeService;
        this.homeServiceRx = homeServiceRx;
    }
    //</editor-fold>

    //<editor-fold desc="Get Home Page Content">
    public Response getHomePageContent(@NotNull UserBaseKey userBaseKey)
    {
        return homeService.getHomePageContent(
                userBaseKey.key,
                Constants.USE_BETA_HOME_PAGE ? true : null);
    }

    @NotNull public Observable<Response> getHomePageContentRx(@NotNull UserBaseKey userBaseKey)
    {
        return homeServiceRx.getHomePageContent(
                userBaseKey.key,
                Constants.USE_BETA_HOME_PAGE ? true : null);
    }
    //</editor-fold>
}
