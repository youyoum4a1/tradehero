package com.tradehero.th.models.discussion;

import com.tradehero.th.api.BaseResponseDTO;
import com.tradehero.th.api.discussion.key.MessageHeaderId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.models.DTOProcessor;
import com.tradehero.th.persistence.home.HomeContentCacheRx;
import com.tradehero.th.persistence.message.MessageHeaderCache;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import org.jetbrains.annotations.NotNull;

public class DTOProcessorMessageRead implements DTOProcessor<BaseResponseDTO>
{
    @NotNull private final MessageHeaderCache messageHeaderCache;
    @NotNull private final UserProfileCacheRx userProfileCache;
    @NotNull private final HomeContentCacheRx homeContentCache;
    @NotNull private MessageHeaderId messageHeaderId;
    @NotNull private UserBaseKey readerId;

    //<editor-fold desc="Constructors">
    public DTOProcessorMessageRead(
            @NotNull MessageHeaderCache messageHeaderCache,
            @NotNull UserProfileCacheRx userProfileCache,
            @NotNull HomeContentCacheRx homeContentCache,
            @NotNull MessageHeaderId messageHeaderId,
            @NotNull UserBaseKey readerId)
    {
        this.messageHeaderCache = messageHeaderCache;
        this.userProfileCache = userProfileCache;
        this.homeContentCache = homeContentCache;
        this.messageHeaderId = messageHeaderId;
        this.readerId = readerId;
    }
    //</editor-fold>

    @Override public BaseResponseDTO process(BaseResponseDTO value)
    {
        messageHeaderCache.setUnread(messageHeaderId, false);
        userProfileCache.get(readerId);
        homeContentCache.invalidate(readerId);
        return value;
    }
}
