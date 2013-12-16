package com.tradehero.th.network.service;

import com.tradehero.th.api.alert.AlertCompactDTO;
import com.tradehero.th.api.alert.AlertDTO;
import com.tradehero.th.api.alert.AlertFormDTO;
import com.tradehero.th.api.alert.AlertId;
import com.tradehero.th.utils.DaggerUtils;
import javax.inject.Inject;
import retrofit.Callback;
import retrofit.RetrofitError;

/**
 * Repurposes requests
 * Created by xavier on 12/12/13.
 */
public class AlertServiceWrapper
{
    public static final String TAG = AlertServiceWrapper.class.getSimpleName();

    @Inject AlertService alertService;

    public AlertServiceWrapper()
    {
        super();
        DaggerUtils.inject(this);
    }

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
            throws RetrofitError
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

    //<editor-fold desc="Update Alert">
    public AlertCompactDTO updateAlert(AlertId alertId, AlertFormDTO alertFormDTO)
            throws RetrofitError
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
