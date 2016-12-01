package com.androidth.general.network.service;

import com.androidth.general.api.alert.AlertCompactDTO;
import com.androidth.general.api.alert.AlertCompactDTOList;
import com.androidth.general.api.alert.AlertDTO;
import com.androidth.general.api.alert.AlertFormDTO;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import rx.Observable;

interface AlertServiceRx
{
    //<editor-fold desc="Get Alerts">
    @GET("api/users/{userId}/alerts")
    Observable<AlertCompactDTOList> getAlerts(
            @Path("userId") int userId);
    //</editor-fold>

    //<editor-fold desc="Get Alert">
    @GET("api/users/{userId}/alerts/{alertId}")
    Observable<AlertDTO> getAlert(
            @Path("userId") int userId,
            @Path("alertId") int alertId);
    //</editor-fold>

    //<editor-fold desc="Create Alert">
    @POST("api/users/{userId}/alerts")
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
