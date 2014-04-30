package com.tradehero.th.models.discussion;

import com.tradehero.th.api.discussion.key.MessageHeaderId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.persistence.message.MessageHeaderCache;
import com.tradehero.th.persistence.message.MessageHeaderListCache;
import com.tradehero.th.persistence.user.UserProfileCache;
import retrofit.client.Response;

public class DTOProcessorMessageDeleted extends DTOProcessorMessageRead
{
    private final MessageHeaderListCache messageHeaderListCache;
    private final MessageHeaderId messageHeaderId;

    public DTOProcessorMessageDeleted(
            MessageHeaderCache messageHeaderCache,
            UserProfileCache userProfileCache,
            MessageHeaderListCache messageHeaderListCache,
            MessageHeaderId messageHeaderId,
            UserBaseKey readerId)
    {
        super(messageHeaderCache, userProfileCache, messageHeaderId, readerId);
        this.messageHeaderListCache = messageHeaderListCache;
        this.messageHeaderId = messageHeaderId;
    }

    @Override public Response process(Response value)
    {
        Response processed = super.process(value);
        messageHeaderListCache.invalidateKeysThatList(messageHeaderId);
        return processed;
    }
}
