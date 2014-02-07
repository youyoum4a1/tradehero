package com.tradehero.th.network.service;

import com.tradehero.th.api.alert.AlertCompactDTO;
import com.tradehero.th.api.alert.AlertDTO;
import com.tradehero.th.api.alert.AlertFormDTO;
import com.tradehero.th.api.alert.AlertId;
import com.tradehero.th.api.users.UserBaseKey;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import retrofit.Callback;

/**
 * Repurposes requests
 * Created by xavier on 12/12/13.
 */
@Singleton public class AlertServiceWrapper
{
    public static final String TAG = AlertServiceWrapper.class.getSimpleName();

    @Inject AlertService alertService;

    @Inject public AlertServiceWrapper()
    {
        super();
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
        alertService.getAlerts(userBaseKey.key, callback);
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

    public void getAlert(AlertId alertId, Callback<AlertDTO> callback)
    {
        basicCheck(alertId);
        this.alertService.getAlert(alertId.userId, alertId.alertId, callback);
    }
    //</editor-fold>

    //<editor-fold desc="Create Alert">
    public AlertCompactDTO createAlert(UserBaseKey userBaseKey, AlertFormDTO alertFormDTO)
    {
        basicCheck(userBaseKey);
        return this.alertService.createAlert(userBaseKey.key, alertFormDTO);
    }

    public void createAlert(UserBaseKey userBaseKey, AlertFormDTO alertFormDTO, Callback<AlertCompactDTO> callback)
    {
        basicCheck(userBaseKey);
        this.alertService.createAlert(userBaseKey.key, alertFormDTO, callback);
    }
    //</editor-fold>

    //<editor-fold desc="Update Alert">
    public AlertCompactDTO updateAlert(AlertId alertId, AlertFormDTO alertFormDTO)
    {
        basicCheck(alertId);
        return this.alertService.updateAlert(alertId.userId, alertId.alertId, alertFormDTO);
    }

    public void updateAlert(AlertId alertId, AlertFormDTO alertFormDTO, Callback<AlertCompactDTO> callback)
    {
        basicCheck(alertId);
        this.alertService.updateAlert(alertId.userId, alertId.alertId, alertFormDTO, callback);
    }
    //</editor-fold>
}
