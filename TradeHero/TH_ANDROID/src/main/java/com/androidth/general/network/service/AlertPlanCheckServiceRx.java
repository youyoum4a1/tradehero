package com.androidth.general.network.service;

import com.androidth.general.api.alert.AlertPlanStatusDTO;

import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;
import rx.Observable;

interface AlertPlanCheckServiceRx
{
    @Deprecated // TODO set in server
    @GET("/users/{userId}/alertPlans/checkAmazon")
    Observable<AlertPlanStatusDTO> checkAlertPlanAttribution(
            @Path("userId") int userId,
            @Query("purchaseToken") String purchaseToken,
            @Query("amazonUserId") String amazonUserId);
}
