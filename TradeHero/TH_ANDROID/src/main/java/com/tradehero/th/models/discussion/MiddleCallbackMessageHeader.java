package com.tradehero.th.models.discussion;

import com.tradehero.th.api.discussion.MessageHeaderDTO;
import com.tradehero.th.network.retrofit.MiddleCallback;
import retrofit.Callback;

/**
 * Created by xavier2 on 2014/4/9.
 */
public class MiddleCallbackMessageHeader extends MiddleCallback<MessageHeaderDTO>
{
    public MiddleCallbackMessageHeader(Callback<MessageHeaderDTO> primaryCallback)
    {
        super(primaryCallback);
    }
}
