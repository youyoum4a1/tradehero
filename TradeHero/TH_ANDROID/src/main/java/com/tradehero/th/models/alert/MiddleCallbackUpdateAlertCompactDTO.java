package com.tradehero.th.models.alert;

import com.tradehero.th.api.alert.AlertCompactDTO;
import com.tradehero.th.api.alert.AlertId;
import com.tradehero.th.network.retrofit.MiddleCallback;
import com.tradehero.th.persistence.alert.AlertCache;
import com.tradehero.th.persistence.alert.AlertCompactCache;
import com.tradehero.th.utils.DaggerUtils;
import javax.inject.Inject;
import retrofit.Callback;
import retrofit.client.Response;

/**
 * Created by xavier on 3/5/14.
 */
public class MiddleCallbackUpdateAlertCompactDTO extends MiddleCallbackAlertCompactDTO
{
    public static final String TAG = MiddleCallbackUpdateAlertCompactDTO.class.getSimpleName();

    private final AlertId alertId;

    public MiddleCallbackUpdateAlertCompactDTO(AlertId alertId, Callback<AlertCompactDTO> primaryCallback)
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
