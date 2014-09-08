package com.tradehero.th.network.service;

import com.tradehero.th.api.alert.AlertPlanStatusDTO;
import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

interface AlertPlanCheckServiceAsync
{
    @Deprecated // TODO set in server
    @GET("/users/{userId}/alertPlans/checkAmazon")
    void checkAlertPlanAttribution(
            @Path("userId") int userId,
            @Query("purchaseToken") String purchaseToken,
            @Query("amazonUserId") String amazonUserId,
            Callback<AlertPlanStatusDTO> callback);
    //</editor-fold>
}
