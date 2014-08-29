package com.tradehero.th.network.service;

import com.tradehero.th.api.alert.AlertPlanStatusDTO;
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

@Singleton public class AlertPlanServiceWrapperAmazon extends AlertPlanServiceWrapper
{
    //<editor-fold desc="Constructors">
    @Inject public AlertPlanServiceWrapperAmazon(
            @NotNull AlertPlanService alertPlanService,
            @NotNull AlertPlanServiceAsync alertPlanServiceAsync,
            @NotNull UserProfileCache userProfileCache)
    {
        super(alertPlanService, alertPlanServiceAsync, userProfileCache);
    }

    //<editor-fold desc="Check Alert Plan Attribution">
    public AlertPlanStatusDTO checkAlertPlanAttribution(
            @NotNull UserBaseKey userBaseKey,
            @NotNull PurchaseReportDTO purchaseReportDTO)
    {
        AlertPlanStatusDTO received;
        // TODO
        throw new IllegalArgumentException("Unhandled type " + purchaseReportDTO.getClass());
    }

    @NotNull public MiddleCallback<AlertPlanStatusDTO> checkAlertPlanAttribution(
            @NotNull UserBaseKey userBaseKey,
            @NotNull PurchaseReportDTO purchaseReportDTO,
            @Nullable Callback<AlertPlanStatusDTO> callback)
    {
        MiddleCallback<AlertPlanStatusDTO> middleCallback = new BaseMiddleCallback<>(callback);
        throw new IllegalArgumentException("Unhandled type " + purchaseReportDTO.getClass());
    }
    //</editor-fold>
}
