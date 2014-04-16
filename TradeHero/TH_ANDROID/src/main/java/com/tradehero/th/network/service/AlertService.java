package com.tradehero.th.network.service;

import com.tradehero.th.api.alert.AlertCompactDTO;
import com.tradehero.th.api.alert.AlertDTO;
import com.tradehero.th.api.alert.AlertFormDTO;
import java.util.List;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;

/** Created with IntelliJ IDEA. User: xavier Date: 11/13/13 Time: 12:54 PM To change this template use File | Settings | File Templates. */
public interface AlertService
{
    //<editor-fold desc="Get Alerts">
    @GET("/users/{userId}/alerts")
    List<AlertCompactDTO> getAlerts(
            @Path("userId") int userId);
    //</editor-fold>

    //<editor-fold desc="Get Alert">
    @GET("/users/{userId}/alerts/{alertId}")
    AlertDTO getAlert(
            @Path("userId") int userId,
            @Path("alertId") int alertId);
    //</editor-fold>

    //<editor-fold desc="Create Alert">
    @POST("/users/{userId}/alerts")
    AlertCompactDTO createAlert(
            @Path("userId") int userId,
            @Body AlertFormDTO alertFormDTO);
    //</editor-fold>

    //<editor-fold desc="Update Alert">
    @PUT("/users/{userId}/alerts/{alertId}")
    AlertCompactDTO updateAlert(
            @Path("userId") int userId,
            @Path("alertId") int alertId,
            @Body AlertFormDTO alertFormDTO);
    //</editor-fold>
}
