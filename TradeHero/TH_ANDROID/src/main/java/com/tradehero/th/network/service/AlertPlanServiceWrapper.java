package com.tradehero.th.network.service;

import com.tradehero.common.billing.googleplay.GooglePlayPurchaseDTO;
import com.tradehero.common.billing.samsung.SamsungPurchaseDTO;
import com.tradehero.th.api.alert.AlertPlanStatusDTO;
import com.tradehero.th.api.users.UserBaseKey;
import javax.inject.Inject;
import javax.inject.Singleton;
import retrofit.Callback;

/**
 * Created by xavier on 2/12/14.
 */
@Singleton public class AlertPlanServiceWrapper
{
    private final AlertPlanService alertPlanService;

    @Inject public AlertPlanServiceWrapper( AlertPlanService alertPlanService)
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

    @Deprecated // TODO set in server
    public AlertPlanStatusDTO checkAlertPlanAttribution(
            UserBaseKey userBaseKey,
            SamsungPurchaseDTO purchaseDTO)
    {
        return alertPlanService.checkAlertPlanAttributionSamsung(
                userBaseKey.key,
                purchaseDTO.paymentId,
                purchaseDTO.productCode);
    }

    @Deprecated // TODO set in server
    public void checkAlertPlanAttribution(
            UserBaseKey userBaseKey,
            SamsungPurchaseDTO purchaseDTO,
            Callback<AlertPlanStatusDTO> callback)
    {
        alertPlanService.checkAlertPlanAttributionSamsung(
                userBaseKey.key,
                purchaseDTO.paymentId,
                purchaseDTO.productCode,
                callback);
    }
    //</editor-fold>
}
