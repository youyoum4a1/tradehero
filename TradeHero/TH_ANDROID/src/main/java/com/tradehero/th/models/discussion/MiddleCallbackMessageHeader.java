package com.tradehero.th.models.discussion;

import com.tradehero.th.api.discussion.MessageHeaderDTO;
import com.tradehero.th.network.retrofit.MiddleCallback;
import retrofit.Callback;

public class MiddleCallbackMessageHeader extends MiddleCallback<MessageHeaderDTO>
{
    public MiddleCallbackMessageHeader(Callback<MessageHeaderDTO> primaryCallback)
    {
        super(primaryCallback);
    }
}
