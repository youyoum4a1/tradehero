package com.ayondo.academy.network.service;

import com.ayondo.academy.api.alert.AlertCompactDTO;
import com.ayondo.academy.api.alert.AlertCompactDTOList;
import com.ayondo.academy.api.alert.AlertDTO;
import com.ayondo.academy.api.alert.AlertFormDTO;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import rx.Observable;

interface AlertServiceRx
{
    //<editor-fold desc="Get Alerts">
    @GET("/users/{userId}/alerts")
    Observable<AlertCompactDTOList> getAlerts(
            @Path("userId") int userId);
    //</editor-fold>

    //<editor-fold desc="Get Alert">
    @GET("/users/{userId}/alerts/{alertId}")
    Observable<AlertDTO> getAlert(
            @Path("userId") int userId,
            @Path("alertId") int alertId);
    //</editor-fold>

    //<editor-fold desc="Create Alert">
    @POST("/users/{userId}/alerts")
    Observable<AlertCompactDTO> createAlert(
            @Path("userId") int userId,
            @Body AlertFormDTO alertFormDTO);
    //</editor-fold>

    //<editor-fold desc="Update Alert">
    @PUT("/users/{userId}/alerts/{alertId}")
    Observable<AlertCompactDTO> updateAlert(
            @Path("userId") int userId,
            @Path("alertId") int alertId,
            @Body AlertFormDTO alertFormDTO);
    //</editor-fold>
}
