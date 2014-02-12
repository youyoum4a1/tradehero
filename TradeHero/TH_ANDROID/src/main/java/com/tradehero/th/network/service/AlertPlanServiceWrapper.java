package com.tradehero.th.network.service;

import com.tradehero.common.billing.googleplay.GooglePlayPurchaseDTO;
import com.tradehero.th.api.alert.AlertPlanStatusDTO;
import com.tradehero.th.api.users.UserBaseKey;
import javax.inject.Inject;
import javax.inject.Singleton;
import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by xavier on 2/12/14.
 */
@Singleton public class AlertPlanServiceWrapper
{
    public static final String TAG = AlertPlanServiceWrapper.class.getSimpleName();

    @Inject protected AlertPlanService alertPlanService;

    @Inject public AlertPlanServiceWrapper()
    {
        super();
    }

    //<editor-fold desc="Check Alert Plan Attribution">
    public AlertPlanStatusDTO checkAlertPlanAttribution(
            UserBaseKey userBaseKey,
            GooglePlayPurchaseDTO googlePlayPurchaseDTO)
    {
        return alertPlanService.checkAlertPlanAttribution(
                userBaseKey.key,
                googlePlayPurchaseDTO.google_play_data,
                googlePlayPurchaseDTO.google_play_signature);
    }

    public void checkAlertPlanAttribution(
            UserBaseKey userBaseKey,
            GooglePlayPurchaseDTO googlePlayPurchaseDTO,
            Callback<AlertPlanStatusDTO> callback)
    {
        alertPlanService.checkAlertPlanAttribution(
                userBaseKey.key,
                googlePlayPurchaseDTO.google_play_data,
                googlePlayPurchaseDTO.google_play_signature,
                callback);
    }
    //</editor-fold>
}
