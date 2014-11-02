package com.tradehero.th.models.discussion;

import com.tradehero.th.api.BaseResponseDTO;
import com.tradehero.th.api.discussion.key.MessageHeaderId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.persistence.home.HomeContentCacheRx;
import com.tradehero.th.persistence.message.MessageHeaderCache;
import com.tradehero.th.persistence.message.MessageHeaderListCache;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import org.jetbrains.annotations.NotNull;

public class DTOProcessorMessageDeleted extends DTOProcessorMessageRead
{
    @NotNull private final MessageHeaderListCache messageHeaderListCache;
    @NotNull private final MessageHeaderId messageHeaderId;

    //<editor-fold desc="Constructors">
    public DTOProcessorMessageDeleted(
            @NotNull MessageHeaderCache messageHeaderCache,
            @NotNull UserProfileCacheRx userProfileCache,
            @NotNull HomeContentCacheRx homeContentCache,
            @NotNull MessageHeaderListCache messageHeaderListCache,
            @NotNull MessageHeaderId messageHeaderId,
            @NotNull UserBaseKey readerId)
    {
        super(messageHeaderCache, userProfileCache, homeContentCache, messageHeaderId, readerId);
        this.messageHeaderListCache = messageHeaderListCache;
        this.messageHeaderId = messageHeaderId;
    }
    //</editor-fold>

    @Override public BaseResponseDTO process(BaseResponseDTO value)
    {
        BaseResponseDTO processed = super.process(value);
        messageHeaderListCache.invalidateKeysThatList(messageHeaderId);
        return processed;
    }
}
