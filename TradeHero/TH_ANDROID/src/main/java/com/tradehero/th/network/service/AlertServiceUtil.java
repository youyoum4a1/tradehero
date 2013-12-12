package com.tradehero.th.network.service;

import com.sun.swing.internal.plaf.basic.resources.basic;
import com.tradehero.th.api.alert.AlertCompactDTO;
import com.tradehero.th.api.alert.AlertDTO;
import com.tradehero.th.api.alert.AlertFormDTO;
import com.tradehero.th.api.alert.AlertId;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.http.Body;
import retrofit.http.Path;

/**
 * Repurposes requests
 * Created by xavier on 12/12/13.
 */
public class AlertServiceUtil
{
    public static final String TAG = AlertServiceUtil.class.getSimpleName();

    private static void basicCheck(AlertId alertId)
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
    public static AlertDTO getAlert(AlertService alertService, AlertId alertId)
            throws RetrofitError
    {
        basicCheck(alertId);
        return alertService.getAlert(alertId.userId, alertId.alertId);
    }

    public static void getAlert(AlertService alertService, AlertId alertId, Callback<AlertDTO> callback)
    {
        basicCheck(alertId);
        alertService.getAlert(alertId.userId, alertId.alertId, callback);
    }
    //</editor-fold>

    //<editor-fold desc="Update Alert">
    public static AlertCompactDTO updateAlert(AlertService alertService, AlertId alertId, AlertFormDTO alertFormDTO)
            throws RetrofitError
    {
        basicCheck(alertId);
        return alertService.updateAlert(alertId.userId, alertId.alertId, alertFormDTO);
    }

    public static void updateAlert(AlertService alertService, AlertId alertId, AlertFormDTO alertFormDTO, Callback<AlertCompactDTO> callback)
    {
        basicCheck(alertId);
        alertService.updateAlert(alertId.userId, alertId.alertId, alertFormDTO, callback);
    }
    //</editor-fold>
}
