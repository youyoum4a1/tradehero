package com.tradehero.th.network.service;

import com.tradehero.th.api.alert.AlertCompactDTO;
import com.tradehero.th.api.alert.AlertDTO;
import com.tradehero.th.api.alert.AlertFormDTO;
import com.tradehero.th.api.alert.AlertId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.models.alert.MiddleCallbackCreateAlertCompact;
import com.tradehero.th.models.alert.MiddleCallbackUpdateAlertCompact;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import retrofit.Callback;


@Singleton public class AlertServiceWrapper
{
    private final AlertService alertService;
    private final AlertServiceAsync alertServiceAsync;

    @Inject public AlertServiceWrapper(AlertService alertService, AlertServiceAsync alertServiceAsync)
    {
        super();
        this.alertService = alertService;
        this.alertServiceAsync = alertServiceAsync;
    }

    private void basicCheck(UserBaseKey userBaseKey)
    {
        if (userBaseKey == null)
        {
            throw new NullPointerException("userBaseKey cannot be null");
        }
        if (userBaseKey.key == null)
        {
            throw new NullPointerException("userBaseKey.key cannot be null");
        }
    }

    //<editor-fold desc="Get Alerts">
    public List<AlertCompactDTO> getAlerts(UserBaseKey userBaseKey)
    {
        basicCheck(userBaseKey);
        return alertService.getAlerts(userBaseKey.key);
    }

    public void getAlerts(UserBaseKey userBaseKey, Callback<List<AlertCompactDTO>> callback)
    {
        basicCheck(userBaseKey);
        alertServiceAsync.getAlerts(userBaseKey.key, callback);
    }
    //</editor-fold>

    private void basicCheck(AlertId alertId)
    {
        if (alertId == null)
        {
            throw new NullPointerException("alertId cannot be null");
        }
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
    public AlertDTO getAlert(AlertId alertId)
    {
        basicCheck(alertId);
        return this.alertService.getAlert(alertId.userId, alertId.alertId);
    }
    //</editor-fold>

    //<editor-fold desc="Create Alert">
    public AlertCompactDTO createAlert(UserBaseKey userBaseKey, AlertFormDTO alertFormDTO)
    {
        basicCheck(userBaseKey);
        return this.alertService.createAlert(userBaseKey.key, alertFormDTO);
    }

    public MiddleCallbackCreateAlertCompact createAlert(UserBaseKey userBaseKey, AlertFormDTO alertFormDTO, Callback<AlertCompactDTO> callback)
    {
        basicCheck(userBaseKey);
        MiddleCallbackCreateAlertCompact middleCallback = new MiddleCallbackCreateAlertCompact(userBaseKey, callback);
        this.alertServiceAsync.createAlert(userBaseKey.key, alertFormDTO, callback);
        return middleCallback;
    }
    //</editor-fold>

    //<editor-fold desc="Update Alert">
    public AlertCompactDTO updateAlert(AlertId alertId, AlertFormDTO alertFormDTO)
    {
        basicCheck(alertId);
        return this.alertService.updateAlert(alertId.userId, alertId.alertId, alertFormDTO);
    }

    public MiddleCallbackUpdateAlertCompact updateAlert(AlertId alertId, AlertFormDTO alertFormDTO, Callback<AlertCompactDTO> callback)
    {
        basicCheck(alertId);
        MiddleCallbackUpdateAlertCompact middleCallback = new MiddleCallbackUpdateAlertCompact(alertId, callback);
        this.alertServiceAsync.updateAlert(alertId.userId, alertId.alertId, alertFormDTO, middleCallback);
        return middleCallback;
    }
    //</editor-fold>
}
