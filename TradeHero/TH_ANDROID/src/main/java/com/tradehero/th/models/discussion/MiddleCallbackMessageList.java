package com.tradehero.th.models.discussion;

import com.tradehero.th.api.PaginatedDTO;
import com.tradehero.th.api.discussion.MessageHeaderDTO;
import com.tradehero.th.network.retrofit.MiddleCallback;
import retrofit.Callback;

/**
 * Created by xavier2 on 2014/4/9.
 */
public class MiddleCallbackMessageList extends MiddleCallback<PaginatedDTO<MessageHeaderDTO>>
{
    public MiddleCallbackMessageList(Callback<PaginatedDTO<MessageHeaderDTO>> primaryCallback)
    {
        super(primaryCallback);
    }
}
