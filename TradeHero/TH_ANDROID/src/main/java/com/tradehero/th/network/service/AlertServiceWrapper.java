package com.tradehero.th.network.service;

import com.tradehero.th.api.alert.AlertCompactDTO;
import com.tradehero.th.api.alert.AlertCompactDTOList;
import com.tradehero.th.api.alert.AlertDTO;
import com.tradehero.th.api.alert.AlertFormDTO;
import com.tradehero.th.api.alert.AlertId;
import com.tradehero.th.api.users.UserBaseKey;
import javax.inject.Inject;
import javax.inject.Singleton;
import android.support.annotation.NonNull;
import rx.Observable;

@Singleton public class AlertServiceWrapper
{
    @NonNull private final AlertServiceRx alertServiceRx;

    @Inject public AlertServiceWrapper(
            @NonNull AlertServiceRx alertServiceRx)
    {
        super();
        this.alertServiceRx = alertServiceRx;
    }

    private void basicCheck(@NonNull UserBaseKey userBaseKey)
    {
        if (userBaseKey.key == null)
        {
            throw new NullPointerException("userBaseKey.key cannot be null");
        }
    }

    //<editor-fold desc="Get Alerts">
    public Observable<AlertCompactDTOList> getAlertsRx(@NonNull UserBaseKey userBaseKey)
    {
        basicCheck(userBaseKey);
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
    public Observable<AlertDTO> getAlertRx(@NonNull AlertId alertId)
    {
        basicCheck(alertId);
        return this.alertServiceRx.getAlert(alertId.userId, alertId.alertId);
    }
    //</editor-fold>

    //<editor-fold desc="Create Alert">
    public Observable<AlertCompactDTO> createAlertRx(@NonNull UserBaseKey userBaseKey, @NonNull AlertFormDTO alertFormDTO)
    {
        basicCheck(userBaseKey);
        return this.alertServiceRx.createAlert(userBaseKey.key, alertFormDTO);
    }
    //</editor-fold>

    //<editor-fold desc="Update Alert">
    public Observable<AlertCompactDTO> updateAlertRx(@NonNull AlertId alertId, @NonNull AlertFormDTO alertFormDTO)
    {
        basicCheck(alertId);
        return this.alertServiceRx.updateAlert(alertId.userId, alertId.alertId, alertFormDTO);
    }
    //</editor-fold>
}
