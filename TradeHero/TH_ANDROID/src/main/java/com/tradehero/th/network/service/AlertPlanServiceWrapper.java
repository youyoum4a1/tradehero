package com.tradehero.th.network.service;

import com.tradehero.th.api.alert.AlertPlanDTOList;
import com.tradehero.th.api.billing.PurchaseReportDTO;
import com.tradehero.th.api.users.RestorePurchaseForm;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.models.DTOProcessor;
import com.tradehero.th.models.user.DTOProcessorUpdateUserProfile;
import com.tradehero.th.network.retrofit.BaseMiddleCallback;
import com.tradehero.th.network.retrofit.MiddleCallback;
import com.tradehero.th.persistence.home.HomeContentCacheRx;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import javax.inject.Inject;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import retrofit.Callback;
import rx.Observable;

public class AlertPlanServiceWrapper
{
    @NonNull protected final AlertPlanService alertPlanService;
    @NonNull protected final AlertPlanServiceAsync alertPlanServiceAsync;
    @NonNull protected final AlertPlanServiceRx alertPlanServiceRx;
    @NonNull protected final UserProfileCacheRx userProfileCache;
    @NonNull protected final HomeContentCacheRx homeContentCache;

    //<editor-fold desc="Constructors">
    @Inject public AlertPlanServiceWrapper(
            @NonNull AlertPlanService alertPlanService,
            @NonNull AlertPlanServiceAsync alertPlanServiceAsync,
            @NonNull AlertPlanServiceRx alertPlanServiceRx,
            @NonNull UserProfileCacheRx userProfileCache,
            @NonNull HomeContentCacheRx homeContentCache)
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
    public Observable<AlertPlanDTOList> getAlertPlansRx(@NonNull UserBaseKey userBaseKey)
    {
        return alertPlanServiceRx.getAlertPlans(userBaseKey.key);
    }
    //</editor-fold>

    //<editor-fold desc="Subscribe to Alert Plan">
    public MiddleCallback<UserProfileDTO> subscribeToAlertPlan(
            @NonNull UserBaseKey userBaseKey,
            @NonNull PurchaseReportDTO purchaseDTO,
            @Nullable Callback<UserProfileDTO> callback)
    {
        MiddleCallback<UserProfileDTO> middleCallback = new BaseMiddleCallback<>(callback, createDTOProcessorUserProfile());
        alertPlanServiceAsync.subscribeToAlertPlan(userBaseKey.key, purchaseDTO, middleCallback);
        return middleCallback;
    }

    public Observable<UserProfileDTO> subscribeToAlertPlanRx(
            @NonNull UserBaseKey userBaseKey,
            @NonNull PurchaseReportDTO purchaseDTO)
    {
        return alertPlanServiceRx.subscribeToAlertPlan(userBaseKey.key, purchaseDTO);
    }
    //</editor-fold>

    //<editor-fold desc="Check Alert Plan Subscription">
    public MiddleCallback<UserProfileDTO> checkAlertPlanSubscription(
            @NonNull UserBaseKey userBaseKey,
            Callback<UserProfileDTO> callback)
    {
        MiddleCallback<UserProfileDTO> middleCallback = new BaseMiddleCallback<>(callback);
        alertPlanServiceAsync.checkAlertPlanSubscription(userBaseKey.key, middleCallback);
        return middleCallback;
    }

    public Observable<UserProfileDTO> checkAlertPlanSubscriptionRx(
            @NonNull UserBaseKey userBaseKey)
    {
        return alertPlanServiceRx.checkAlertPlanSubscription(userBaseKey.key);
    }
    //</editor-fold>

    //<editor-fold desc="Restore Purchases">
    public Observable<UserProfileDTO> restorePurchasesRx(
            @NonNull UserBaseKey userBaseKey,
            @NonNull RestorePurchaseForm restorePurchaseForm)
    {
        return alertPlanServiceRx.restorePurchases(userBaseKey.key, restorePurchaseForm);
    }
    //</editor-fold>

    protected DTOProcessor<UserProfileDTO> createDTOProcessorUserProfile()
    {
        return new DTOProcessorUpdateUserProfile(userProfileCache, homeContentCache);
    }
}
