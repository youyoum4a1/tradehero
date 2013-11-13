package com.tradehero.th.network.service;

import com.tradehero.th.api.purchase.PurchaseDTO;
import com.tradehero.th.api.alert.AlertPlanDTO;
import com.tradehero.th.api.users.RestorePurchaseForm;
import com.tradehero.th.api.users.UserProfileDTO;
import java.util.List;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;

/** Created with IntelliJ IDEA. User: xavier Date: 11/13/13 Time: 11:30 AM To change this template use File | Settings | File Templates. */
public interface AlertPlanService
{
    //<editor-fold desc="Get Alert Plans">
    @GET("/users/{userId}/alertPlans")
    List<AlertPlanDTO> getAlertPlans(
            @Path("userId") int userId)
        throws RetrofitError;

    @GET("/users/{userId}/alertPlans")
    void getAlertPlans(
            @Path("userId") int userId,
            Callback<List<AlertPlanDTO>> callback);
    //</editor-fold>

    //<editor-fold desc="Subscribe To Alert Plans">
    @POST("/users/{userId}/alertPlans")
    UserProfileDTO subscribeToAlertPlan(
            @Path("userId") int userId,
            @Body PurchaseDTO purchaseDTO)
        throws RetrofitError;

    @POST("/users/{userId}/alertPlans")
    void subscribeToAlertPlan(
            @Path("userId") int userId,
            @Body PurchaseDTO purchaseDTO,
            Callback<UserProfileDTO> callback);
    //</editor-fold>

    //<editor-fold desc="Check Alert Plan Subscription">
    @POST("/users/{userId}/alertPlans/checkAlertPlanSubscription")
    UserProfileDTO checkAlertPlanSubscription(
            @Path("userId") int userId)
        throws RetrofitError;

    @POST("/users/{userId}/alertPlans/checkAlertPlanSubscription")
    void checkAlertPlanSubscription(
            @Path("userId") int userId,
            Callback<UserProfileDTO> callback);
    //</editor-fold>

    //<editor-fold desc="Restore Purchases">
    @POST("/users/{userId}/alertPlans/restore")
    UserProfileDTO restorePurchases(
            @Path("userId") int userId,
            @Body RestorePurchaseForm restorePurchaseForm)
        throws RetrofitError;

    @POST("/users/{userId}/alertPlans/restore")
    void restorePurchases(
            @Path("userId") int userId,
            @Body RestorePurchaseForm restorePurchaseForm,
            Callback<UserProfileDTO> callback);
    //</editor-fold>
}
