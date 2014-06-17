package com.tradehero.th.network.service;

import com.tradehero.common.billing.googleplay.GooglePlayPurchaseDTO;
import com.tradehero.th.api.alert.AlertPlanStatusDTO;
import com.tradehero.th.api.users.UserBaseKey;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import retrofit.Callback;

@Singleton public class AlertPlanServiceWrapper
{
    @NotNull private final AlertPlanService alertPlanService;

    @Inject public AlertPlanServiceWrapper(@NotNull AlertPlanService alertPlanService)
    {
        super();
        this.alertPlanService = alertPlanService;
    }

    //<editor-fold desc="Check Alert Plan Attribution">
    public AlertPlanStatusDTO checkAlertPlanAttribution(
            UserBaseKey userBaseKey,
            GooglePlayPurchaseDTO googlePlayPurchaseDTO)
    {
        return alertPlanService.checkAlertPlanAttribution(
                userBaseKey.key,
                googlePlayPurchaseDTO.googlePlayData,
                googlePlayPurchaseDTO.googlePlaySignature);
    }

    public void checkAlertPlanAttribution(
            UserBaseKey userBaseKey,
            GooglePlayPurchaseDTO googlePlayPurchaseDTO,
            Callback<AlertPlanStatusDTO> callback)
    {
        alertPlanService.checkAlertPlanAttribution(
                userBaseKey.key,
                googlePlayPurchaseDTO.googlePlayData,
                googlePlayPurchaseDTO.googlePlaySignature,
                callback);
    }
    //</editor-fold>
}
