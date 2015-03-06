package com.tradehero.th.network.service;

import com.tradehero.common.billing.googleplay.GooglePlayPurchaseDTO;
import com.tradehero.th.api.alert.AlertPlanStatusDTO;
import com.tradehero.th.api.users.UserProfileDTO;
import retrofit.http.*;

public interface AlertPlanService
{

    //<editor-fold desc="Subscribe To Alert Plan">
    @POST("/users/{userId}/alertPlans")
    UserProfileDTO subscribeToAlertPlan(
            @Path("userId") int userId,
            @Body GooglePlayPurchaseDTO purchaseDTO);
    //</editor-fold>

    //<editor-fold desc="Check Alert Plan Subscription">
    @POST("/users/{userId}/alertPlans/checkAlertPlanSubscription")
    UserProfileDTO checkAlertPlanSubscription(
            @Path("userId") int userId);
    //</editor-fold>

    //<editor-fold desc="Check Alert Plan Attribution">
    @GET("/users/{userId}/alertPlans/check")
    AlertPlanStatusDTO checkAlertPlanAttribution(
            @Path("userId") int userId,
            @Query("google_play_data") String googlePlayData,
            @Query("google_play_signature") String googlePlaySignature);
    //</editor-fold>

}
