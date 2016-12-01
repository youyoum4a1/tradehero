package com.androidth.general.network.service;

import com.androidth.general.api.alert.AlertPlanDTOList;
import com.androidth.general.api.billing.PurchaseReportDTO;
import com.androidth.general.api.users.RestorePurchaseForm;
import com.androidth.general.api.users.UserProfileDTO;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import rx.Observable;

interface AlertPlanServiceRx
{
    //<editor-fold desc="Get Alert Plans">
    @GET("api/users/{userId}/alertPlans")
    Observable<AlertPlanDTOList> getAlertPlans(
            @Path("userId") int userId);
    //</editor-fold>

    //<editor-fold desc="Subscribe To Alert Plan">
    @POST("api/users/{userId}/alertPlans")
    Observable<UserProfileDTO> subscribeToAlertPlan(
            @Path("userId") int userId,
            @Body PurchaseReportDTO purchaseReportDTO);
    //</editor-fold>

    //<editor-fold desc="Check Alert Plan Subscription">
    @POST("api/users/{userId}/alertPlans/checkAlertPlanSubscription")
    Observable<UserProfileDTO> checkAlertPlanSubscription(
            @Path("userId") int userId);
    //</editor-fold>

    //<editor-fold desc="Restore Purchases">
    @POST("api/users/{userId}/alertPlans/restore")
    Observable<UserProfileDTO> restorePurchases(
            @Path("userId") int userId,
            @Body RestorePurchaseForm restorePurchaseForm);
    //</editor-fold>
}
