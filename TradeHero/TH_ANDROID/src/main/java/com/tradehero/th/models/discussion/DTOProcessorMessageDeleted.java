package com.ayondo.academy.models.discussion;

import android.support.annotation.NonNull;
import com.ayondo.academy.api.BaseResponseDTO;
import com.ayondo.academy.api.discussion.key.MessageHeaderId;
import com.ayondo.academy.api.users.UserBaseKey;
import com.ayondo.academy.persistence.message.MessageHeaderCacheRx;
import com.ayondo.academy.persistence.message.MessageHeaderListCacheRx;
import com.ayondo.academy.persistence.user.UserProfileCacheRx;

public class DTOProcessorMessageDeleted extends DTOProcessorMessageRead
{
    @NonNull private final MessageHeaderListCacheRx messageHeaderListCache;
    @NonNull private final MessageHeaderId messageHeaderId;

    //<editor-fold desc="Constructors">
    public DTOProcessorMessageDeleted(
            @NonNull MessageHeaderCacheRx messageHeaderCache,
            @NonNull UserProfileCacheRx userProfileCache,
            @NonNull MessageHeaderListCacheRx messageHeaderListCache,
            @NonNull MessageHeaderId messageHeaderId,
            @NonNull UserBaseKey readerId)
    {
        super(messageHeaderCache, userProfileCache, messageHeaderId, readerId);
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
