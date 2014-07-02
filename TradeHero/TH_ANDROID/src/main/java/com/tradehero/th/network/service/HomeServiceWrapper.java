package com.tradehero.th.network.service;

import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.network.retrofit.BaseMiddleCallback;
import com.tradehero.th.network.retrofit.MiddleCallback;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import retrofit.Callback;
import retrofit.client.Response;

public class HomeServiceWrapper
{
    @NotNull private final HomeService homeService;
    @NotNull private final HomeServiceAsync homeServiceAsync;

    //<editor-fold desc="Constructors">
    @Inject public HomeServiceWrapper(@NotNull HomeService homeService, @NotNull HomeServiceAsync homeServiceAsync)
    {
        this.homeService = homeService;
        this.homeServiceAsync = homeServiceAsync;
    }
    //</editor-fold>

    //<editor-fold desc="Get Home Page Content">
    public Response getHomePageContent(@NotNull UserBaseKey userBaseKey)
    {
        return homeService.getHomePageContent(userBaseKey.key);
    }

    public MiddleCallback<Response> getHomePageContent(@NotNull UserBaseKey userBaseKey, @Nullable Callback<Response> callback)
    {
        MiddleCallback<Response> middleCallback = new BaseMiddleCallback<>(callback);
        homeServiceAsync.getHomePageContent(userBaseKey.key, middleCallback);
        return middleCallback;
    }
    //</editor-fold>
}
