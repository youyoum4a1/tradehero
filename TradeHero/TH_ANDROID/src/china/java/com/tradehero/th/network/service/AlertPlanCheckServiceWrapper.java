package com.tradehero.th.network.service;

import com.tradehero.th.api.alert.AlertPlanStatusDTO;
import com.tradehero.th.api.billing.PurchaseReportDTO;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.network.retrofit.BaseMiddleCallback;
import com.tradehero.th.network.retrofit.MiddleCallback;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import javax.inject.Inject;
import javax.inject.Singleton;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import retrofit.Callback;

@Singleton public class AlertPlanCheckServiceWrapper
{
    @NonNull protected final AlertPlanCheckService alertPlanCheckService;
    @NonNull protected final AlertPlanCheckServiceAsync alertPlanCheckServiceAsync;
    @NonNull protected final UserProfileCacheRx userProfileCache;

    //<editor-fold desc="Constructors">
    @Inject public AlertPlanCheckServiceWrapper(
            @NonNull AlertPlanCheckService alertPlanCheckService,
            @NonNull AlertPlanCheckServiceAsync alertPlanCheckServiceAsync,
            @NonNull UserProfileCacheRx userProfileCache)
    {
        super();
        this.alertPlanCheckService = alertPlanCheckService;
        this.alertPlanCheckServiceAsync = alertPlanCheckServiceAsync;
        this.userProfileCache = userProfileCache;
    }

    //<editor-fold desc="Check Alert Plan Attribution">
    public AlertPlanStatusDTO checkAlertPlanAttribution(
            @NonNull UserBaseKey userBaseKey,
            @NonNull PurchaseReportDTO purchaseReportDTO)
    {
        return alertPlanCheckService.checkAlertPlanAttribution(
                userBaseKey.getUserId());
    }

    @NonNull public MiddleCallback<AlertPlanStatusDTO> checkAlertPlanAttribution(
            @NonNull UserBaseKey userBaseKey,
            @NonNull PurchaseReportDTO purchaseReportDTO,
            @Nullable Callback<AlertPlanStatusDTO> callback)
    {
        MiddleCallback<AlertPlanStatusDTO> middleCallback = new BaseMiddleCallback<>(callback);
        alertPlanCheckServiceAsync.checkAlertPlanAttribution(
                userBaseKey.getUserId(),
                middleCallback);
        return middleCallback;
    }
    //</editor-fold>
}
