package com.tradehero.th.network.service;

import com.tradehero.common.billing.googleplay.GooglePlayPurchaseDTO;
import com.tradehero.th.api.alert.AlertPlanStatusDTO;
import com.tradehero.th.api.users.UserProfileDTO;
import retrofit.Callback;
import retrofit.http.*;

public interface AlertPlanServiceAsync
{

    //<editor-fold desc="Subscribe To Alert Plan">
    @POST("/users/{userId}/alertPlans")
    void subscribeToAlertPlan(
            @Path("userId") int userId,
            @Body GooglePlayPurchaseDTO purchaseDTO,
            Callback<UserProfileDTO> callback);
    //</editor-fold>

    //<editor-fold desc="Check Alert Plan Subscription">
    @POST("/users/{userId}/alertPlans/checkAlertPlanSubscription")
    void checkAlertPlanSubscription(
            @Path("userId") int userId,
            Callback<UserProfileDTO> callback);
    //</editor-fold>

    //<editor-fold desc="Check Alert Plan Attribution">
    @GET("/users/{userId}/alertPlans/check")
    void checkAlertPlanAttribution(
            @Path("userId") int userId,
            @Query("google_play_data") String googlePlayData,
            @Query("google_play_signature") String googlePlaySignature,
            Callback<AlertPlanStatusDTO> callback);
    //</editor-fold>

}
