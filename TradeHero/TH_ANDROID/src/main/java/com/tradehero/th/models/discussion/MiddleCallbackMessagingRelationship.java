package com.tradehero.th.models.discussion;

import com.tradehero.th.api.users.UserMessagingRelationshipDTO;
import com.tradehero.th.network.retrofit.BaseMiddleCallback;
import retrofit.Callback;

public class MiddleCallbackMessagingRelationship extends BaseMiddleCallback<UserMessagingRelationshipDTO>
{
    public MiddleCallbackMessagingRelationship(
            Callback<UserMessagingRelationshipDTO> primaryCallback)
    {
        super(primaryCallback);
    }
}
