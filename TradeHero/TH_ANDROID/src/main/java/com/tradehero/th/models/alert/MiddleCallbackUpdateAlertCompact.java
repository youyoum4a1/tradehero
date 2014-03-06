package com.tradehero.th.models.alert;

import com.tradehero.th.api.alert.AlertCompactDTO;
import com.tradehero.th.api.alert.AlertId;
import com.tradehero.th.utils.DaggerUtils;
import retrofit.Callback;
import retrofit.client.Response;

/**
 * Created by xavier on 3/5/14.
 */
public class MiddleCallbackUpdateAlertCompact extends MiddleCallbackAlertCompact
{
    public static final String TAG = MiddleCallbackUpdateAlertCompact.class.getSimpleName();

    private final AlertId alertId;

    public MiddleCallbackUpdateAlertCompact(AlertId alertId, Callback<AlertCompactDTO> primaryCallback)
    {
        super(primaryCallback);
        this.alertId = alertId;
        DaggerUtils.inject(this);
    }

    @Override public void success(AlertCompactDTO alertCompactDTO, Response response)
    {
        alertCompactCache.put(alertId, alertCompactDTO);
        alertCache.invalidate(alertId);
        super.success(alertCompactDTO, response);
    }
}
