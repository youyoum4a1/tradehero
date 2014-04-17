package com.tradehero.th.models.discussion;

import com.tradehero.th.api.users.UserMessagingRelationshipDTO;
import com.tradehero.th.network.retrofit.MiddleCallback;
import retrofit.Callback;

public class MiddleCallbackMessagingRelationship extends MiddleCallback<UserMessagingRelationshipDTO>
{
    public MiddleCallbackMessagingRelationship(
            Callback<UserMessagingRelationshipDTO> primaryCallback)
    {
        super(primaryCallback);
    }
}
