package com.tradehero.th.models.discussion;

import com.tradehero.th.api.discussion.key.MessageHeaderId;
import com.tradehero.th.network.retrofit.BaseMiddleCallback;
import com.tradehero.th.persistence.message.MessageHeaderListCache;
import retrofit.Callback;
import retrofit.client.Response;

public class MiddleCallbackMessageDeleted extends BaseMiddleCallback<Response>
{
    private final MessageHeaderId messageHeaderId;
    private final MessageHeaderListCache messageHeaderListCache;

    public MiddleCallbackMessageDeleted(
            MessageHeaderId messageHeaderId,
            Callback<Response> primaryCallback,
            MessageHeaderListCache messageHeaderListCache)
    {
        super(primaryCallback);
        this.messageHeaderId = messageHeaderId;
        this.messageHeaderListCache = messageHeaderListCache;
    }

    @Override public void success(Response response, Response response2)
    {
        invalidateCache();
        super.success(response, response2);
    }

    private void invalidateCache()
    {
        if (messageHeaderListCache != null)
        {
            messageHeaderListCache.invalidateKeysThatList(messageHeaderId);
        }
    }
}
