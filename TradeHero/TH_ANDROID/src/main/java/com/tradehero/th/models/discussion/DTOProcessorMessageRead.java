package com.tradehero.th.models.discussion;

import com.tradehero.th.api.discussion.MessageHeaderDTO;
import com.tradehero.th.api.discussion.key.MessageHeaderId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.models.DTOProcessor;
import com.tradehero.th.persistence.message.MessageHeaderCache;
import com.tradehero.th.persistence.user.UserProfileCache;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import retrofit.client.Response;

public class DTOProcessorMessageRead implements DTOProcessor<Response>
{
    @NotNull private final MessageHeaderCache messageHeaderCache;
    @NotNull private final UserProfileCache userProfileCache;
    @Nullable private final MessageHeaderId messageHeaderId;
    @Nullable private final UserBaseKey readerId;

    //<editor-fold desc="Constructors">
    public DTOProcessorMessageRead(
            @NotNull MessageHeaderCache messageHeaderCache,
            @NotNull UserProfileCache userProfileCache,
            @Nullable MessageHeaderId messageHeaderId,
            @Nullable UserBaseKey readerId)
    {
        this.messageHeaderCache = messageHeaderCache;
        this.userProfileCache = userProfileCache;
        this.messageHeaderId = messageHeaderId;
        this.readerId = readerId;
    }
    //</editor-fold>

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
            userProfileCache.getOrFetchAsync(readerId, true);
        }
        return value;
    }
}
