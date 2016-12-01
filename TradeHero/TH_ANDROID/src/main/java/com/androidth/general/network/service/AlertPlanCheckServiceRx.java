package com.androidth.general.network.service;

import com.androidth.general.api.alert.AlertPlanStatusDTO;

import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

interface AlertPlanCheckServiceRx
{
    @Deprecated // TODO set in server
    @GET("api/users/{userId}/alertPlans/checkAmazon")
    Observable<AlertPlanStatusDTO> checkAlertPlanAttribution(
            @Path("userId") int userId,
            @Query("purchaseToken") String purchaseToken,
            @Query("amazonUserId") String amazonUserId);
}
