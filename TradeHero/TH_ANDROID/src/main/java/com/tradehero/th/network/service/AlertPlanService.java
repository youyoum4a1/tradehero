package com.tradehero.th.network.service;

import com.tradehero.th.api.alert.AlertPlanDTO;
import com.tradehero.th.api.alert.AlertPlanStatusDTO;
import com.tradehero.th.api.billing.PurchaseReportDTO;
import com.tradehero.th.api.users.RestorePurchaseForm;
import com.tradehero.th.api.users.UserProfileDTO;
import java.util.List;
import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;

public interface AlertPlanService
{
    //<editor-fold desc="Get Alert Plans">
    @GET("/users/{userId}/alertPlans")
    List<AlertPlanDTO> getAlertPlans(
            @Path("userId") int userId);

    @GET("/users/{userId}/alertPlans")
    void getAlertPlans(
            @Path("userId") int userId,
            Callback<List<AlertPlanDTO>> callback);
    //</editor-fold>

    //<editor-fold desc="Subscribe To Alert Plans">
    @POST("/users/{userId}/alertPlans")
    UserProfileDTO subscribeToAlertPlan(
            @Path("userId") int userId,
            @Body PurchaseReportDTO purchaseReportDTO);

    @POST("/users/{userId}/alertPlans")
    void subscribeToAlertPlan(
            @Path("userId") int userId,
            @Body PurchaseReportDTO purchaseReportDTO,
            Callback<UserProfileDTO> callback);
    //</editor-fold>

    //<editor-fold desc="Check Alert Plan Subscription">
    @POST("/users/{userId}/alertPlans/checkAlertPlanSubscription")
    UserProfileDTO checkAlertPlanSubscription(
            @Path("userId") int userId);

    @POST("/users/{userId}/alertPlans/checkAlertPlanSubscription")
    void checkAlertPlanSubscription(
            @Path("userId") int userId,
            Callback<UserProfileDTO> callback);
    //</editor-fold>

    //<editor-fold desc="Check Alert Plan Attribution">
    @GET("/users/{userId}/alertPlans/check")
    AlertPlanStatusDTO checkAlertPlanAttribution(
            @Path("userId") int userId,
            @Query("google_play_data") String googlePlayData,
            @Query("google_play_signature") String googlePlaySignature);

    @GET("/users/{userId}/alertPlans/check")
    void checkAlertPlanAttribution(
            @Path("userId") int userId,
            @Query("google_play_data") String googlePlayData,
            @Query("google_play_signature") String googlePlaySignature,
            Callback<AlertPlanStatusDTO> callback);

    @Deprecated // TODO set in server
    @GET("/users/{userId}/alertPlans/checkSamsung")
    AlertPlanStatusDTO checkAlertPlanAttributionSamsung(
            @Path("userId") int userId,
            @Query("paymentId") String paymentId,
            @Query("productCode") String productCode);

    @Deprecated // TODO set in server
    @GET("/users/{userId}/alertPlans/checkSamsung")
    void checkAlertPlanAttributionSamsung(
            @Path("userId") int userId,
            @Query("paymentId") String paymentId,
            @Query("productCode") String productCode,
            Callback<AlertPlanStatusDTO> callback);
    //</editor-fold>

    //<editor-fold desc="Restore Purchases">
    @POST("/users/{userId}/alertPlans/restore")
    UserProfileDTO restorePurchases(
            @Path("userId") int userId,
            @Body RestorePurchaseForm restorePurchaseForm);

    @POST("/users/{userId}/alertPlans/restore")
    void restorePurchases(
            @Path("userId") int userId,
            @Body RestorePurchaseForm restorePurchaseForm,
            Callback<UserProfileDTO> callback);
    //</editor-fold>
}
