package com.tradehero.th.network.service;

import com.tradehero.th.api.alert.AlertCompactDTO;
import com.tradehero.th.api.alert.AlertDTO;
import com.tradehero.th.api.alert.AlertFormDTO;
import java.util.List;
import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;


interface AlertServiceAsync
{
    //<editor-fold desc="Get Alerts">
    @GET("/users/{userId}/alerts")
    void getAlerts(
            @Path("userId") int userId,
            Callback<List<AlertCompactDTO>> callback);
    //</editor-fold>

    //<editor-fold desc="Get Alert">
    @GET("/users/{userId}/alerts/{alertId}")
    void getAlert(
            @Path("userId") int userId,
            @Path("alertId") int alertId,
            Callback<AlertDTO> callback);
    //</editor-fold>

    //<editor-fold desc="Create Alert">
    @POST("/users/{userId}/alerts")
    void createAlert(
            @Path("userId") int userId,
            @Body AlertFormDTO alertFormDTO,
            Callback<AlertCompactDTO> callback);
    //</editor-fold>

    //<editor-fold desc="Update Alert">
    @PUT("/users/{userId}/alerts/{alertId}")
    void updateAlert(
            @Path("userId") int userId,
            @Path("alertId") int alertId,
            @Body AlertFormDTO alertFormDTO,
            Callback<AlertCompactDTO> callback);
    //</editor-fold>
}
