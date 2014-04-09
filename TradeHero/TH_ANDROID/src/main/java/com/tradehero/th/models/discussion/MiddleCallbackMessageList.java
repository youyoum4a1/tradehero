package com.tradehero.th.models.discussion;

import com.tradehero.th.api.PaginatedDTO;
import com.tradehero.th.api.discussion.MessageDTO;
import com.tradehero.th.network.retrofit.MiddleCallback;
import retrofit.Callback;

/**
 * Created by xavier2 on 2014/4/9.
 */
public class MiddleCallbackMessageList extends MiddleCallback<PaginatedDTO<MessageDTO>>
{
    public MiddleCallbackMessageList(Callback<PaginatedDTO<MessageDTO>> primaryCallback)
    {
        super(primaryCallback);
    }
}
