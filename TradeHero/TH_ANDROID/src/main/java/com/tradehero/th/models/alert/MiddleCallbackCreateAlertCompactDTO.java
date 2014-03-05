package com.tradehero.th.models.alert;

import com.tradehero.th.api.alert.AlertCompactDTO;
import com.tradehero.th.api.alert.AlertId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.utils.DaggerUtils;
import retrofit.Callback;
import retrofit.client.Response;

/**
 * Created by xavier on 3/5/14.
 */
public class MiddleCallbackCreateAlertCompactDTO extends MiddleCallbackAlertCompactDTO
{
    public static final String TAG = MiddleCallbackCreateAlertCompactDTO.class.getSimpleName();

    private final UserBaseKey userBaseKey;

    public MiddleCallbackCreateAlertCompactDTO(UserBaseKey userBaseKey, Callback<AlertCompactDTO> primaryCallback)
    {
        super(primaryCallback);
        this.userBaseKey = userBaseKey;
        DaggerUtils.inject(this);
    }

    @Override public void success(AlertCompactDTO alertCompactDTO, Response response)
    {
        AlertId alertId = alertCompactDTO.getAlertId(userBaseKey);
        alertCompactListCache.invalidate(userBaseKey);
        alertCompactCache.put(alertId, alertCompactDTO);
        alertCache.invalidate(alertId);
        super.success(alertCompactDTO, response);
    }
}
