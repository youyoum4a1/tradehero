package com.tradehero.th.models.discussion;

import com.tradehero.th.api.discussion.MessageStatusDTO;
import com.tradehero.th.network.retrofit.MiddleCallback;
import retrofit.Callback;

/**
 * Created by xavier2 on 2014/4/10.
 */
public class MiddleCallbackMessageStatus extends MiddleCallback<MessageStatusDTO>
{
    public MiddleCallbackMessageStatus(Callback<MessageStatusDTO> primaryCallback)
    {
        super(primaryCallback);
    }
}
