package com.ayondo.academy.network.service;

import com.ayondo.academy.api.alert.AlertPlanStatusDTO;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;
import rx.Observable;

interface AlertPlanCheckServiceRx
{
    //<editor-fold desc="Check Alert Plan Attribution">
    @GET("/users/{userId}/alertPlans/check")
    Observable<AlertPlanStatusDTO> checkAlertPlanAttribution(
            @Path("userId") int userId,
            @Query("google_play_data") String googlePlayData,
            @Query("google_play_signature") String googlePlaySignature);
    //</editor-fold>
}
