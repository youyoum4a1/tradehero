package com.androidth.general.network.service;

import com.androidth.general.api.alert.AlertPlanStatusDTO;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;
import rx.Observable;

public interface AlertPlanCheckServiceRx
{
    @Deprecated // TODO set in server
    @GET("/users/{userId}/alertPlans/checkSamsung")
    Observable<AlertPlanStatusDTO> checkAlertPlanAttribution(
            @Path("userId") int userId,
            @Query("paymentId") String paymentId,
            @Query("productCode") String productCode);
    //</editor-fold>
}
