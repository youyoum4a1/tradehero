package com.tradehero.th.network.service;

import com.tradehero.th.api.alert.AlertPlanStatusDTO;
import com.tradehero.th.api.billing.GooglePlayPurchaseReportDTO;
import com.tradehero.th.api.billing.PurchaseReportDTO;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.network.retrofit.BaseMiddleCallback;
import com.tradehero.th.network.retrofit.MiddleCallback;
import com.tradehero.th.persistence.user.UserProfileCache;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import retrofit.Callback;

@Singleton public class AlertPlanCheckServiceWrapper
{
    @NotNull protected final AlertPlanCheckService alertPlanCheckService;
    @NotNull protected final AlertPlanCheckServiceAsync alertPlanCheckServiceAsync;
    @NotNull protected final UserProfileCache userProfileCache;

    //<editor-fold desc="Constructors">
    @Inject public AlertPlanCheckServiceWrapper(
            @NotNull AlertPlanCheckService alertPlanCheckService,
            @NotNull AlertPlanCheckServiceAsync alertPlanCheckServiceAsync,
            @NotNull UserProfileCache userProfileCache)
    {
        super();
        this.alertPlanCheckService = alertPlanCheckService;
        this.alertPlanCheckServiceAsync = alertPlanCheckServiceAsync;
        this.userProfileCache = userProfileCache;
    }

    //<editor-fold desc="Check Alert Plan Attribution">
    public AlertPlanStatusDTO checkAlertPlanAttribution(
            @NotNull UserBaseKey userBaseKey,
            @NotNull PurchaseReportDTO purchaseReportDTO)
    {
        AlertPlanStatusDTO received;
        received = alertPlanCheckService.checkAlertPlanAttribution(
                userBaseKey.key,
                ((GooglePlayPurchaseReportDTO) purchaseReportDTO).googlePlayData,
                ((GooglePlayPurchaseReportDTO) purchaseReportDTO).googlePlaySignature);
        return received;
    }

    @NotNull public MiddleCallback<AlertPlanStatusDTO> checkAlertPlanAttribution(
            @NotNull UserBaseKey userBaseKey,
            @NotNull PurchaseReportDTO purchaseReportDTO,
            @Nullable Callback<AlertPlanStatusDTO> callback)
    {
        MiddleCallback<AlertPlanStatusDTO> middleCallback = new BaseMiddleCallback<>(callback);
        alertPlanCheckServiceAsync.checkAlertPlanAttribution(
                userBaseKey.key,
                ((GooglePlayPurchaseReportDTO) purchaseReportDTO).googlePlayData,
                ((GooglePlayPurchaseReportDTO) purchaseReportDTO).googlePlaySignature,
                middleCallback);
        return middleCallback;
    }
    //</editor-fold>
}
