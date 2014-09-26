package com.tradehero.th.models.discussion;

import com.tradehero.th.api.BaseResponseDTO;
import com.tradehero.th.api.discussion.MessageHeaderDTO;
import com.tradehero.th.api.discussion.key.MessageHeaderId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.models.DTOProcessor;
import com.tradehero.th.persistence.message.MessageHeaderCache;
import com.tradehero.th.persistence.user.UserProfileCache;
import org.jetbrains.annotations.NotNull;

public class DTOProcessorMessageRead implements DTOProcessor<BaseResponseDTO>
{
    @NotNull private final MessageHeaderCache messageHeaderCache;
    @NotNull private final UserProfileCache userProfileCache;
    @NotNull private MessageHeaderId messageHeaderId;
    @NotNull private UserBaseKey readerId;

    //<editor-fold desc="Constructors">
    public DTOProcessorMessageRead(
            @NotNull MessageHeaderCache messageHeaderCache,
            @NotNull UserProfileCache userProfileCache,
            @NotNull MessageHeaderId messageHeaderId,
            @NotNull UserBaseKey readerId)
    {
        this.messageHeaderCache = messageHeaderCache;
        this.userProfileCache = userProfileCache;
        this.messageHeaderId = messageHeaderId;
        this.readerId = readerId;
    }
    //</editor-fold>

    @Override public BaseResponseDTO process(BaseResponseDTO value)
    {
        MessageHeaderDTO messageHeaderDTO = messageHeaderCache.get(messageHeaderId);
        if (messageHeaderDTO != null && messageHeaderDTO.unread)
        {
            messageHeaderDTO.unread = false;
        }
        userProfileCache.getOrFetchAsync(readerId, true);
        return value;
    }
}
