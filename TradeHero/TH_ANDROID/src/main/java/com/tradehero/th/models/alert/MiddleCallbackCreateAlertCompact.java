package com.tradehero.th.models.alert;

import com.tradehero.th.api.alert.AlertCompactDTO;
import com.tradehero.th.api.alert.AlertId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.utils.DaggerUtils;
import retrofit.Callback;
import retrofit.client.Response;

public class MiddleCallbackCreateAlertCompact extends MiddleCallbackAlertCompact
{
    private final UserBaseKey userBaseKey;

    public MiddleCallbackCreateAlertCompact(UserBaseKey userBaseKey, Callback<AlertCompactDTO> primaryCallback)
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
