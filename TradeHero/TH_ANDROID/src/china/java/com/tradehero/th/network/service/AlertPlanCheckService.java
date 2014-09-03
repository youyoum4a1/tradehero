package com.tradehero.th.network.service;

import com.tradehero.th.api.alert.AlertPlanStatusDTO;
import retrofit.http.GET;
import retrofit.http.Path;

public interface AlertPlanCheckService
{
    @Deprecated // TODO set in server
    @GET("/users/{userId}/alertPlans/checkChina")
    AlertPlanStatusDTO checkAlertPlanAttribution(
            @Path("userId") int userId);
}
