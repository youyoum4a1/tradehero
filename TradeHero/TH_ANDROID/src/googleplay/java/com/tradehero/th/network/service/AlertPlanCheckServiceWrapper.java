package com.androidth.general.network.service;

import android.support.annotation.NonNull;
import com.androidth.general.api.alert.AlertPlanStatusDTO;
import com.androidth.general.api.billing.GooglePlayPurchaseReportDTO;
import com.androidth.general.api.billing.PurchaseReportDTO;
import com.androidth.general.api.users.UserBaseKey;
import javax.inject.Inject;
import javax.inject.Singleton;
import rx.Observable;

@Singleton public class AlertPlanCheckServiceWrapper
{
    @NonNull protected final AlertPlanCheckServiceRx alertPlanCheckServiceRx;

    //<editor-fold desc="Constructors">
    @Inject public AlertPlanCheckServiceWrapper(
            @NonNull AlertPlanCheckServiceRx alertPlanCheckServiceRx)
    {
        super();
        this.alertPlanCheckServiceRx = alertPlanCheckServiceRx;
    }

    //<editor-fold desc="Check Alert Plan Attribution">
    public Observable<AlertPlanStatusDTO> checkAlertPlanAttributionRx(
            @NonNull UserBaseKey userBaseKey,
            @NonNull PurchaseReportDTO purchaseReportDTO)
    {
        Observable<AlertPlanStatusDTO> received;
        received = alertPlanCheckServiceRx.checkAlertPlanAttribution(
                userBaseKey.key,
                ((GooglePlayPurchaseReportDTO) purchaseReportDTO).googlePlayData,
                ((GooglePlayPurchaseReportDTO) purchaseReportDTO).googlePlaySignature);
        return received;
    }
    //</editor-fold>
}
