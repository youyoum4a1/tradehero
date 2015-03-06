package com.tradehero.th.network.service;

import com.tradehero.common.billing.googleplay.GooglePlayPurchaseDTO;
import com.tradehero.th.api.alert.AlertPlanStatusDTO;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.models.DTOProcessor;
import com.tradehero.th.models.user.DTOProcessorUpdateUserProfile;
import com.tradehero.th.network.retrofit.BaseMiddleCallback;
import com.tradehero.th.network.retrofit.MiddleCallback;
import com.tradehero.th.persistence.user.UserProfileCache;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import retrofit.Callback;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton public class AlertPlanServiceWrapper
{
    @NotNull private final AlertPlanService alertPlanService;
    @NotNull private final AlertPlanServiceAsync alertPlanServiceAsync;
    @NotNull protected final UserProfileCache userProfileCache;

    @Inject public AlertPlanServiceWrapper(
            @NotNull AlertPlanService alertPlanService,
            @NotNull AlertPlanServiceAsync alertPlanServiceAsync,
            @NotNull UserProfileCache userProfileCache)
    {
        super();
        this.alertPlanService = alertPlanService;
        this.alertPlanServiceAsync = alertPlanServiceAsync;
        this.userProfileCache = userProfileCache;
    }


    //<editor-fold desc="Subscribe to Alert Plan">
    public UserProfileDTO subscribeToAlertPlan(
            @NotNull UserBaseKey userBaseKey,
            @NotNull GooglePlayPurchaseDTO purchaseDTO)
    {
        return createDTOProcessorUserProfile().process(alertPlanService.subscribeToAlertPlan(userBaseKey.key, purchaseDTO));
    }

    public MiddleCallback<UserProfileDTO> subscribeToAlertPlan(
            @NotNull UserBaseKey userBaseKey,
            @NotNull GooglePlayPurchaseDTO purchaseDTO,
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

    //<editor-fold desc="Check Alert Plan Attribution">
    public AlertPlanStatusDTO checkAlertPlanAttribution(
            @NotNull UserBaseKey userBaseKey,
            @NotNull GooglePlayPurchaseDTO googlePlayPurchaseDTO)
    {
        return alertPlanService.checkAlertPlanAttribution(
                userBaseKey.key,
                googlePlayPurchaseDTO.googlePlayData,
                googlePlayPurchaseDTO.googlePlaySignature);
    }

    @NotNull public MiddleCallback<AlertPlanStatusDTO> checkAlertPlanAttribution(
            @NotNull UserBaseKey userBaseKey,
            @NotNull GooglePlayPurchaseDTO googlePlayPurchaseDTO,
            @Nullable Callback<AlertPlanStatusDTO> callback)
    {
        MiddleCallback<AlertPlanStatusDTO> middleCallback = new BaseMiddleCallback<>(callback);
        alertPlanServiceAsync.checkAlertPlanAttribution(
                userBaseKey.key,
                googlePlayPurchaseDTO.googlePlayData,
                googlePlayPurchaseDTO.googlePlaySignature,
                middleCallback);
        return middleCallback;
    }
    //</editor-fold>


    protected DTOProcessor<UserProfileDTO> createDTOProcessorUserProfile()
    {
        return new DTOProcessorUpdateUserProfile(userProfileCache);
    }
}
