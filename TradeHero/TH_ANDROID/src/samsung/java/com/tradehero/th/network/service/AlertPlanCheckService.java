package com.tradehero.th.network.service;

import com.tradehero.th.api.alert.AlertPlanStatusDTO;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

public interface AlertPlanCheckService
{
    @Deprecated // TODO set in server
    @GET("/users/{userId}/alertPlans/checkSamsung")
    AlertPlanStatusDTO checkAlertPlanAttribution(
            @Path("userId") int userId,
            @Query("paymentId") String paymentId,
            @Query("productCode") String productCode);
    //</editor-fold>
}
