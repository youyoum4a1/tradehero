package com.tradehero.th.network.service;

import com.tradehero.th.api.alert.AlertCompactDTOList;
import com.tradehero.th.api.alert.AlertDTO;
import retrofit.http.GET;
import retrofit.http.Path;

public interface AlertService
{
    //<editor-fold desc="Get Alerts">
    @GET("/users/{userId}/alerts") AlertCompactDTOList getAlerts(
            @Path("userId") int userId);
    //</editor-fold>

    //<editor-fold desc="Get Alert">
    @GET("/users/{userId}/alerts/{alertId}")
    AlertDTO getAlert(
            @Path("userId") int userId,
            @Path("alertId") int alertId);
    //</editor-fold>
}
