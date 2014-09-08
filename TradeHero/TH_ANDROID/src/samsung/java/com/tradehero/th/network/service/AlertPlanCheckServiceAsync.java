package com.tradehero.th.network.service;

import com.tradehero.th.api.alert.AlertPlanStatusDTO;
import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

interface AlertPlanCheckServiceAsync
{
    @Deprecated // TODO set in server
    @GET("/users/{userId}/alertPlans/checkSamsung")
    void checkAlertPlanAttribution(
            @Path("userId") int userId,
            @Query("paymentId") String paymentId,
            @Query("productCode") String productCode,
            Callback<AlertPlanStatusDTO> callback);
    //</editor-fold>
}
