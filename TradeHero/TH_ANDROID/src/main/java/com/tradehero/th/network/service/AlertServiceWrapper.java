package com.tradehero.th.network.service;

import android.support.annotation.NonNull;
import com.tradehero.th.api.alert.AlertCompactDTO;
import com.tradehero.th.api.alert.AlertCompactDTOList;
import com.tradehero.th.api.alert.AlertDTO;
import com.tradehero.th.api.alert.AlertFormDTO;
import com.tradehero.th.api.alert.AlertId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.persistence.alert.AlertCacheRx;
import dagger.Lazy;
import javax.inject.Inject;
import javax.inject.Singleton;
import rx.Observable;
import rx.functions.Action1;

@Singleton public class AlertServiceWrapper
{
    @NonNull private final AlertServiceRx alertServiceRx;
    @NonNull private final Lazy<AlertCacheRx> alertCache;

    //<editor-fold desc="Constructors">
    @Inject public AlertServiceWrapper(
            @NonNull AlertServiceRx alertServiceRx,
            @NonNull Lazy<AlertCacheRx> alertCache)
    {
        super();
        this.alertServiceRx = alertServiceRx;
        this.alertCache = alertCache;
    }
    //</editor-fold>

    //<editor-fold desc="Get Alerts">
    @NonNull public Observable<AlertCompactDTOList> getAlertsRx(@NonNull UserBaseKey userBaseKey)
    {
        return alertServiceRx.getAlerts(userBaseKey.key);
    }
    //</editor-fold>

    private void basicCheck(@NonNull AlertId alertId)
    {
        if (alertId.userId == null)
        {
            throw new NullPointerException("alertId.userId cannot be null");
        }
        if (alertId.alertId == null)
        {
            throw new NullPointerException("alertId.alertId cannot be null");
        }
    }

    //<editor-fold desc="Get Alert">
    @NonNull public Observable<AlertDTO> getAlertRx(@NonNull AlertId alertId)
    {
        basicCheck(alertId);
        return this.alertServiceRx.getAlert(alertId.userId, alertId.alertId);
    }
    //</editor-fold>

    //<editor-fold desc="Create Alert">
    @NonNull public Observable<AlertCompactDTO> createAlertRx(@NonNull UserBaseKey userBaseKey, @NonNull AlertFormDTO alertFormDTO)
    {
        return this.alertServiceRx.createAlert(userBaseKey.key, alertFormDTO);
    }
    //</editor-fold>

    //<editor-fold desc="Update Alert">
    @NonNull public Observable<AlertCompactDTO> updateAlertRx(@NonNull final AlertId alertId, @NonNull AlertFormDTO alertFormDTO)
    {
        basicCheck(alertId);
        return this.alertServiceRx.updateAlert(alertId.userId, alertId.alertId, alertFormDTO)
                .doOnNext(new Action1<AlertCompactDTO>()
                {
                    @Override public void call(AlertCompactDTO alertCompactDTO)
                    {
                        alertCache.get().invalidate(alertId);
                    }
                });
    }
    //</editor-fold>
}
