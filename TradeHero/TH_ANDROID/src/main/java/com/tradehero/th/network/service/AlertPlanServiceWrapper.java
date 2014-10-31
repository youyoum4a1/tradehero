package com.tradehero.th.network.service;

import com.tradehero.th.api.alert.AlertPlanDTO;
import com.tradehero.th.api.alert.AlertPlanDTOList;
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
import java.util.List;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import retrofit.Callback;
import rx.Observable;

public class AlertPlanServiceWrapper
{
    @NotNull protected final AlertPlanService alertPlanService;
    @NotNull protected final AlertPlanServiceAsync alertPlanServiceAsync;
    @NotNull protected final AlertPlanServiceRx alertPlanServiceRx;
    @NotNull protected final UserProfileCache userProfileCache;
    @NotNull protected final HomeContentCache homeContentCache;

    //<editor-fold desc="Constructors">
    @Inject public AlertPlanServiceWrapper(
            @NotNull AlertPlanService alertPlanService,
            @NotNull AlertPlanServiceAsync alertPlanServiceAsync,
            @NotNull AlertPlanServiceRx alertPlanServiceRx,
            @NotNull UserProfileCache userProfileCache,
            @NotNull HomeContentCache homeContentCache)
    {
        super();
        this.alertPlanService = alertPlanService;
        this.alertPlanServiceAsync = alertPlanServiceAsync;
        this.alertPlanServiceRx = alertPlanServiceRx;
        this.userProfileCache = userProfileCache;
        this.homeContentCache = homeContentCache;
    }
    //</editor-fold>

    //<editor-fold desc="Get Alert Plans">
    public Observable<AlertPlanDTOList> getAlertPlansRx(@NotNull UserBaseKey userBaseKey)
    {
        return alertPlanServiceRx.getAlertPlans(userBaseKey.key);
    }
    //</editor-fold>

    //<editor-fold desc="Subscribe to Alert Plan">
    public MiddleCallback<UserProfileDTO> subscribeToAlertPlan(
            @NotNull UserBaseKey userBaseKey,
            @NotNull PurchaseReportDTO purchaseDTO,
            @Nullable Callback<UserProfileDTO> callback)
    {
        MiddleCallback<UserProfileDTO> middleCallback = new BaseMiddleCallback<>(callback, createDTOProcessorUserProfile());
        alertPlanServiceAsync.subscribeToAlertPlan(userBaseKey.key, purchaseDTO, middleCallback);
        return middleCallback;
    }

    public Observable<UserProfileDTO> subscribeToAlertPlanRx(
            @NotNull UserBaseKey userBaseKey,
            @NotNull PurchaseReportDTO purchaseDTO)
    {
        return alertPlanServiceRx.subscribeToAlertPlan(userBaseKey.key, purchaseDTO);
    }
    //</editor-fold>

    //<editor-fold desc="Check Alert Plan Subscription">
    public MiddleCallback<UserProfileDTO> checkAlertPlanSubscription(
            @NotNull UserBaseKey userBaseKey,
            Callback<UserProfileDTO> callback)
    {
        MiddleCallback<UserProfileDTO> middleCallback = new BaseMiddleCallback<>(callback);
        alertPlanServiceAsync.checkAlertPlanSubscription(userBaseKey.key, middleCallback);
        return middleCallback;
    }

    public Observable<UserProfileDTO> checkAlertPlanSubscriptionRx(
            @NotNull UserBaseKey userBaseKey)
    {
        return alertPlanServiceRx.checkAlertPlanSubscription(userBaseKey.key);
    }
    //</editor-fold>

    //<editor-fold desc="Restore Purchases">
    public Observable<UserProfileDTO> restorePurchasesRx(
            @NotNull UserBaseKey userBaseKey,
            @NotNull RestorePurchaseForm restorePurchaseForm)
    {
        return alertPlanServiceRx.restorePurchases(userBaseKey.key, restorePurchaseForm);
    }
    //</editor-fold>

    protected DTOProcessor<UserProfileDTO> createDTOProcessorUserProfile()
    {
        return new DTOProcessorUpdateUserProfile(userProfileCache, homeContentCache);
    }
}
