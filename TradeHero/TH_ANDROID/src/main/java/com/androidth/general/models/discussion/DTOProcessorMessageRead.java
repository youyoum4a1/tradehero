package com.androidth.general.models.discussion;

import android.support.annotation.NonNull;
import com.androidth.general.api.BaseResponseDTO;
import com.androidth.general.api.discussion.key.MessageHeaderId;
import com.androidth.general.api.users.UserBaseKey;
import com.androidth.general.models.ThroughDTOProcessor;
import com.androidth.general.persistence.message.MessageHeaderCacheRx;
import com.androidth.general.persistence.user.UserProfileCacheRx;

public class DTOProcessorMessageRead extends ThroughDTOProcessor<BaseResponseDTO>
{
    @NonNull private final MessageHeaderCacheRx messageHeaderCache;
    @NonNull private final UserProfileCacheRx userProfileCache;
    @NonNull private MessageHeaderId messageHeaderId;
    @NonNull private UserBaseKey readerId;

    //<editor-fold desc="Constructors">
    public DTOProcessorMessageRead(
            @NonNull MessageHeaderCacheRx messageHeaderCache,
            @NonNull UserProfileCacheRx userProfileCache,
            @NonNull MessageHeaderId messageHeaderId,
            @NonNull UserBaseKey readerId)
    {
        this.messageHeaderCache = messageHeaderCache;
        this.userProfileCache = userProfileCache;
        this.messageHeaderId = messageHeaderId;
        this.readerId = readerId;
    }
    //</editor-fold>

    @Override public BaseResponseDTO process(BaseResponseDTO value)
    {
        messageHeaderCache.setUnread(messageHeaderId, false);
        userProfileCache.get(readerId);
        return value;
    }
}
