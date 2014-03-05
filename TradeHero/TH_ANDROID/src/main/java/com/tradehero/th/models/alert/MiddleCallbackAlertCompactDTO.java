package com.tradehero.th.models.alert;

import com.tradehero.th.api.alert.AlertCompactDTO;
import com.tradehero.th.api.alert.AlertId;
import com.tradehero.th.network.retrofit.MiddleCallback;
import com.tradehero.th.persistence.alert.AlertCache;
import com.tradehero.th.persistence.alert.AlertCompactCache;
import com.tradehero.th.persistence.alert.AlertCompactListCache;
import com.tradehero.th.utils.DaggerUtils;
import javax.inject.Inject;
import retrofit.Callback;
import retrofit.client.Response;

/**
 * Created by xavier on 3/5/14.
 */
abstract public class MiddleCallbackAlertCompactDTO extends MiddleCallback<AlertCompactDTO>
{
    public static final String TAG = MiddleCallbackAlertCompactDTO.class.getSimpleName();

    @Inject protected AlertCompactListCache alertCompactListCache;
    @Inject protected AlertCompactCache alertCompactCache;
    @Inject protected AlertCache alertCache;

    public MiddleCallbackAlertCompactDTO(Callback<AlertCompactDTO> primaryCallback)
    {
        super(primaryCallback);
        DaggerUtils.inject(this);
    }
}
