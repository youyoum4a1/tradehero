package com.ayondo.academy.network.service;

import android.support.annotation.NonNull;
import com.ayondo.academy.api.alert.AlertPlanStatusDTO;
import com.ayondo.academy.api.billing.AmazonPurchaseReportDTO;
import com.ayondo.academy.api.billing.PurchaseReportDTO;
import com.ayondo.academy.api.users.UserBaseKey;
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
        return alertPlanCheckServiceRx.checkAlertPlanAttribution(
                userBaseKey.getUserId(),
                ((AmazonPurchaseReportDTO) purchaseReportDTO).amazonPurchaseToken,
                ((AmazonPurchaseReportDTO) purchaseReportDTO).amazonUserId);
    }
    //</editor-fold>
}
