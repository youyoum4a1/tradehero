package com.tradehero.th.models.alert;

import com.tradehero.th.api.alert.AlertCompactDTO;
import com.tradehero.th.network.retrofit.BaseMiddleCallback;
import com.tradehero.th.persistence.alert.AlertCache;
import com.tradehero.th.persistence.alert.AlertCompactCache;
import com.tradehero.th.persistence.alert.AlertCompactListCache;
import com.tradehero.th.utils.DaggerUtils;
import javax.inject.Inject;
import retrofit.Callback;

abstract public class MiddleCallbackAlertCompact extends BaseMiddleCallback<AlertCompactDTO>
{
    @Inject protected AlertCompactListCache alertCompactListCache;
    @Inject protected AlertCompactCache alertCompactCache;
    @Inject protected AlertCache alertCache;

    public MiddleCallbackAlertCompact(Callback<AlertCompactDTO> primaryCallback)
    {
        super(primaryCallback);
        DaggerUtils.inject(this);
    }
}
