package com.ayondo.academy.network.service;

import com.ayondo.academy.api.alert.AlertPlanDTOList;
import com.ayondo.academy.api.billing.PurchaseReportDTO;
import com.ayondo.academy.api.users.RestorePurchaseForm;
import com.ayondo.academy.api.users.UserProfileDTO;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import rx.Observable;

interface AlertPlanServiceRx
{
    //<editor-fold desc="Get Alert Plans">
    @GET("/users/{userId}/alertPlans")
    Observable<AlertPlanDTOList> getAlertPlans(
            @Path("userId") int userId);
    //</editor-fold>

    //<editor-fold desc="Subscribe To Alert Plan">
    @POST("/users/{userId}/alertPlans")
    Observable<UserProfileDTO> subscribeToAlertPlan(
            @Path("userId") int userId,
            @Body PurchaseReportDTO purchaseReportDTO);
    //</editor-fold>

    //<editor-fold desc="Check Alert Plan Subscription">
    @POST("/users/{userId}/alertPlans/checkAlertPlanSubscription")
    Observable<UserProfileDTO> checkAlertPlanSubscription(
            @Path("userId") int userId);
    //</editor-fold>

    //<editor-fold desc="Restore Purchases">
    @POST("/users/{userId}/alertPlans/restore")
    Observable<UserProfileDTO> restorePurchases(
            @Path("userId") int userId,
            @Body RestorePurchaseForm restorePurchaseForm);
    //</editor-fold>
}
