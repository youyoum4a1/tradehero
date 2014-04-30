package com.tradehero.th.models.discussion;

import com.tradehero.th.api.discussion.MessageHeaderDTO;
import com.tradehero.th.network.retrofit.BaseMiddleCallback;
import retrofit.Callback;

public class MiddleCallbackMessageHeader extends BaseMiddleCallback<MessageHeaderDTO>
{
    public MiddleCallbackMessageHeader(Callback<MessageHeaderDTO> primaryCallback)
    {
        super(primaryCallback);
    }
}
