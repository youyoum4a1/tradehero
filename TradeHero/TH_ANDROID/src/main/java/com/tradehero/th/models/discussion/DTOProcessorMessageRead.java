package com.tradehero.th.models.discussion;

import com.tradehero.th.api.discussion.MessageHeaderDTO;
import com.tradehero.th.api.discussion.key.MessageHeaderId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.models.DTOProcessor;
import com.tradehero.th.persistence.message.MessageHeaderCache;
import com.tradehero.th.persistence.user.UserProfileCache;
import retrofit.client.Response;

public class DTOProcessorMessageRead implements DTOProcessor<Response>
{
    private final MessageHeaderCache messageHeaderCache;
    private final UserProfileCache userProfileCache;
    private final MessageHeaderId messageHeaderId;
    private final UserBaseKey readerId;

    public DTOProcessorMessageRead(
            MessageHeaderCache messageHeaderCache,
            UserProfileCache userProfileCache,
            MessageHeaderId messageHeaderId,
            UserBaseKey readerId)
    {
        this.messageHeaderCache = messageHeaderCache;
        this.userProfileCache = userProfileCache;
        this.messageHeaderId = messageHeaderId;
        this.readerId = readerId;
    }

    @Override public Response process(Response value)
    {
        if (messageHeaderId != null)
        {
            MessageHeaderDTO messageHeaderDTO = messageHeaderCache.get(messageHeaderId);
            if (messageHeaderDTO != null && messageHeaderDTO.unread)
            {
                messageHeaderDTO.unread = false;
            }
        }
        if (readerId != null)
        {
            UserProfileDTO userProfileDTO = userProfileCache.get(readerId);
            if (userProfileDTO != null && userProfileDTO.unreadMessageThreadsCount > 0)
            {
                --userProfileDTO.unreadMessageThreadsCount;
            }
        }
        return value;
    }
}
