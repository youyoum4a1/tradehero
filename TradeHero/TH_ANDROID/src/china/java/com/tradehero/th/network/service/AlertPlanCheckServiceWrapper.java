package com.tradehero.th.network.service;

import android.support.annotation.NonNull;
import com.tradehero.th.api.alert.AlertPlanStatusDTO;
import com.tradehero.th.api.billing.PurchaseReportDTO;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import javax.inject.Inject;
import javax.inject.Singleton;
import rx.Observable;

@Singleton public class AlertPlanCheckServiceWrapper
{
    @NonNull protected final AlertPlanCheckServiceRx alertPlanCheckServiceRx;
    @NonNull protected final UserProfileCacheRx userProfileCache;

    //<editor-fold desc="Constructors">
    @Inject public AlertPlanCheckServiceWrapper(
            @NonNull AlertPlanCheckServiceRx alertPlanCheckServiceRx,
            @NonNull UserProfileCacheRx userProfileCache)
    {
        super();
        this.alertPlanCheckServiceRx = alertPlanCheckServiceRx;
        this.userProfileCache = userProfileCache;
    }

    //<editor-fold desc="Check Alert Plan Attribution">
    @NonNull public Observable<AlertPlanStatusDTO> checkAlertPlanAttributionRx(
            @NonNull UserBaseKey userBaseKey,
            @NonNull PurchaseReportDTO purchaseReportDTO)
    {
        return alertPlanCheckServiceRx.checkAlertPlanAttribution(
                userBaseKey.getUserId());
    }
    //</editor-fold>
}
