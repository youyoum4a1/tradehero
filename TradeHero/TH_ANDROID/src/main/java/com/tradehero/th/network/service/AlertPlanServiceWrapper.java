package com.tradehero.th.network.service;

import android.support.annotation.NonNull;
import com.tradehero.th.api.alert.AlertPlanDTOList;
import com.tradehero.th.api.billing.PurchaseReportDTO;
import com.tradehero.th.api.users.RestorePurchaseForm;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.models.DTOProcessor;
import com.tradehero.th.models.user.DTOProcessorUpdateUserProfile;
import com.tradehero.th.persistence.home.HomeContentCacheRx;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import javax.inject.Inject;
import rx.Observable;

public class AlertPlanServiceWrapper
{
    @NonNull protected final AlertPlanServiceRx alertPlanServiceRx;
    @NonNull protected final UserProfileCacheRx userProfileCache;
    @NonNull protected final HomeContentCacheRx homeContentCache;

    //<editor-fold desc="Constructors">
    @Inject public AlertPlanServiceWrapper(
            @NonNull AlertPlanServiceRx alertPlanServiceRx,
            @NonNull UserProfileCacheRx userProfileCache,
            @NonNull HomeContentCacheRx homeContentCache)
    {
        super();
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
    public Observable<UserProfileDTO> subscribeToAlertPlanRx(
            @NonNull UserBaseKey userBaseKey,
            @NonNull PurchaseReportDTO purchaseDTO)
    {
        return alertPlanServiceRx.subscribeToAlertPlan(userBaseKey.key, purchaseDTO);
    }
    //</editor-fold>

    //<editor-fold desc="Check Alert Plan Subscription">
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
