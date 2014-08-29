package com.tradehero.th.network.service;

import com.tradehero.th.api.alert.AlertPlanStatusDTO;
import com.tradehero.th.api.billing.PurchaseReportDTO;
import com.tradehero.th.api.billing.SamsungPurchaseReportDTO;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.network.retrofit.BaseMiddleCallback;
import com.tradehero.th.network.retrofit.MiddleCallback;
import com.tradehero.th.persistence.user.UserProfileCache;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import retrofit.Callback;

@Singleton public class AlertPlanServiceWrapperSamsung extends AlertPlanServiceWrapper
{
    //<editor-fold desc="Constructors">
    @Inject public AlertPlanServiceWrapperSamsung(
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
        if (purchaseReportDTO instanceof SamsungPurchaseReportDTO)
        {
            received = alertPlanService.checkAlertPlanAttributionSamsung(
                    userBaseKey.key,
                    ((SamsungPurchaseReportDTO) purchaseReportDTO).paymentId,
                    ((SamsungPurchaseReportDTO) purchaseReportDTO).productCode);
        }
        else
        {
            throw new IllegalArgumentException("Unhandled type " + purchaseReportDTO.getClass());
        }
        return received;
    }

    @NotNull public MiddleCallback<AlertPlanStatusDTO> checkAlertPlanAttribution(
            @NotNull UserBaseKey userBaseKey,
            @NotNull PurchaseReportDTO purchaseReportDTO,
            @Nullable Callback<AlertPlanStatusDTO> callback)
    {
        MiddleCallback<AlertPlanStatusDTO> middleCallback = new BaseMiddleCallback<>(callback);
        if (purchaseReportDTO instanceof SamsungPurchaseReportDTO)
        {
            alertPlanServiceAsync.checkAlertPlanAttributionSamsung(
                    userBaseKey.key,
                    ((SamsungPurchaseReportDTO) purchaseReportDTO).paymentId,
                    ((SamsungPurchaseReportDTO) purchaseReportDTO).productCode,
                    middleCallback);
        }
        else
        {
            throw new IllegalArgumentException("Unhandled type " + purchaseReportDTO.getClass());
        }
        return middleCallback;
    }
    //</editor-fold>
}
