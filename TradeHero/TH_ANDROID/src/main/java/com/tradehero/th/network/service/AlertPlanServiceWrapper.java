package com.tradehero.th.network.service;

import com.tradehero.th.api.alert.AlertPlanDTO;
import com.tradehero.th.api.billing.PurchaseReportDTO;
import com.tradehero.th.api.users.RestorePurchaseForm;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.models.DTOProcessor;
import com.tradehero.th.models.user.DTOProcessorUpdateUserProfile;
import com.tradehero.th.network.retrofit.BaseMiddleCallback;
import com.tradehero.th.network.retrofit.MiddleCallback;
import com.tradehero.th.persistence.home.HomeContentCache;
import com.tradehero.th.persistence.user.UserProfileCache;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import javax.inject.Inject;

import retrofit.Callback;

public class AlertPlanServiceWrapper
{
    @NotNull protected final AlertPlanService alertPlanService;
    @NotNull protected final AlertPlanServiceAsync alertPlanServiceAsync;
    @NotNull protected final UserProfileCache userProfileCache;
    @NotNull protected final HomeContentCache homeContentCache;

    //<editor-fold desc="Constructors">
    @Inject public AlertPlanServiceWrapper(
            @NotNull AlertPlanService alertPlanService,
            @NotNull AlertPlanServiceAsync alertPlanServiceAsync,
            @NotNull UserProfileCache userProfileCache,
            @NotNull HomeContentCache homeContentCache)
    {
        super();
        this.alertPlanService = alertPlanService;
        this.alertPlanServiceAsync = alertPlanServiceAsync;
        this.userProfileCache = userProfileCache;
        this.homeContentCache = homeContentCache;
    }
    //</editor-fold>

    //<editor-fold desc="Get Alert Plans">
    public List<AlertPlanDTO> getAlertPlans(@NotNull UserBaseKey userBaseKey)
    {
        return alertPlanService.getAlertPlans(userBaseKey.key);
    }

    public MiddleCallback<List<AlertPlanDTO>> getAlertPlans(
            @NotNull UserBaseKey userBaseKey,
            @Nullable Callback<List<AlertPlanDTO>> callback)
    {
        MiddleCallback<List<AlertPlanDTO>> middleCallback = new BaseMiddleCallback<>(callback);
        alertPlanServiceAsync.getAlertPlans(userBaseKey.key, middleCallback);
        return middleCallback;
    }
    //</editor-fold>

    //<editor-fold desc="Subscribe to Alert Plan">
    public UserProfileDTO subscribeToAlertPlan(
            @NotNull UserBaseKey userBaseKey,
            @NotNull PurchaseReportDTO purchaseDTO)
    {
        return createDTOProcessorUserProfile().process(alertPlanService.subscribeToAlertPlan(userBaseKey.key, purchaseDTO));
    }

    public MiddleCallback<UserProfileDTO> subscribeToAlertPlan(
            @NotNull UserBaseKey userBaseKey,
            @NotNull PurchaseReportDTO purchaseDTO,
            @Nullable Callback<UserProfileDTO> callback)
    {
        MiddleCallback<UserProfileDTO> middleCallback = new BaseMiddleCallback<>(callback, createDTOProcessorUserProfile());
        alertPlanServiceAsync.subscribeToAlertPlan(userBaseKey.key, purchaseDTO, middleCallback);
        return middleCallback;
    }
    //</editor-fold>

    //<editor-fold desc="Check Alert Plan Subscription">
    public UserProfileDTO checkAlertPlanSubscription(
            @NotNull UserBaseKey userBaseKey)
    {
        return alertPlanService.checkAlertPlanSubscription(userBaseKey.key);
    }

    public MiddleCallback<UserProfileDTO> checkAlertPlanSubscription(
            @NotNull UserBaseKey userBaseKey,
            Callback<UserProfileDTO> callback)
    {
        MiddleCallback<UserProfileDTO> middleCallback = new BaseMiddleCallback<>(callback);
        alertPlanServiceAsync.checkAlertPlanSubscription(userBaseKey.key, middleCallback);
        return middleCallback;
    }
    //</editor-fold>

    //<editor-fold desc="Restore Purchases">
    public UserProfileDTO restorePurchases(
            @NotNull UserBaseKey userBaseKey,
            @NotNull RestorePurchaseForm restorePurchaseForm)
    {
        return createDTOProcessorUserProfile().process(alertPlanService.restorePurchases(userBaseKey.key, restorePurchaseForm));
    }

    public MiddleCallback<UserProfileDTO> restorePurchases(
            @NotNull UserBaseKey userBaseKey,
            @NotNull RestorePurchaseForm restorePurchaseForm,
            @Nullable Callback<UserProfileDTO> callback)
    {
        MiddleCallback<UserProfileDTO> middleCallback = new BaseMiddleCallback<>(callback, createDTOProcessorUserProfile());
        alertPlanServiceAsync.restorePurchases(userBaseKey.key, restorePurchaseForm, middleCallback);
        return middleCallback;
    }
    //</editor-fold>

    protected DTOProcessor<UserProfileDTO> createDTOProcessorUserProfile()
    {
        return new DTOProcessorUpdateUserProfile(userProfileCache, homeContentCache);
    }
}
