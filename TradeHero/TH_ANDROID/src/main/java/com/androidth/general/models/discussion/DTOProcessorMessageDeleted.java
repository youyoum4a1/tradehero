package com.androidth.general.models.discussion;

import android.support.annotation.NonNull;
import com.androidth.general.api.BaseResponseDTO;
import com.androidth.general.api.discussion.key.MessageHeaderId;
import com.androidth.general.api.users.UserBaseKey;
import com.androidth.general.persistence.message.MessageHeaderCacheRx;
import com.androidth.general.persistence.message.MessageHeaderListCacheRx;
import com.androidth.general.persistence.user.UserProfileCacheRx;

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
