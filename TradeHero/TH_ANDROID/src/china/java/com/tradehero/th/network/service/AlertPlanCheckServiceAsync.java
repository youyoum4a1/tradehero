package com.tradehero.th.network.service;

import com.tradehero.th.api.alert.AlertPlanStatusDTO;
import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;

interface AlertPlanCheckServiceAsync
{
    @Deprecated // TODO set in server
    @GET("/users/{userId}/alertPlans/checkChina")
    void checkAlertPlanAttribution(
            @Path("userId") int userId,
            Callback<AlertPlanStatusDTO> callback);
    //</editor-fold>
}
