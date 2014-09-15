package com.tradehero.th.network.service;

import com.tradehero.th.api.alert.AlertPlanStatusDTO;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

interface AlertPlanCheckServiceAsync
{
    //<editor-fold desc="Check Alert Plan Attribution">
    @GET("/users/{userId}/alertPlans/check")
    void checkAlertPlanAttribution(
            @Path("userId") int userId,
            @Query("google_play_data") String googlePlayData,
            @Query("google_play_signature") String googlePlaySignature,
            Callback<AlertPlanStatusDTO> callback);
    //</editor-fold>
}

