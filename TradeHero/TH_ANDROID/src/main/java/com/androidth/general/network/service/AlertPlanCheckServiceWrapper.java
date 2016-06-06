package com.androidth.general.network.service;

import android.support.annotation.NonNull;

import com.androidth.general.api.alert.AlertPlanStatusDTO;
import com.androidth.general.api.billing.PurchaseReportDTO;
import com.androidth.general.api.users.UserBaseKey;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;

//import com.androidth.general.api.billing.AmazonPurchaseReportDTO;

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
        return null;
//        return alertPlanCheckServiceRx.checkAlertPlanAttribution(
//                userBaseKey.getUserId(),
//                ((AmazonPurchaseReportDTO) purchaseReportDTO).amazonPurchaseToken,
//                ((AmazonPurchaseReportDTO) purchaseReportDTO).amazonUserId);
    }
    //</editor-fold>
}
