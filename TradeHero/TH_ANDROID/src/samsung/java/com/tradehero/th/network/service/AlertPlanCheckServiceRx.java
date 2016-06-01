package com.ayondo.academy.network.service;

import com.ayondo.academy.api.alert.AlertPlanStatusDTO;
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
