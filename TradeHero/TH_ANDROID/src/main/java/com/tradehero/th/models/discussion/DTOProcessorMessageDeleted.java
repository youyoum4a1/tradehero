package com.tradehero.th.models.discussion;

import com.tradehero.th.api.BaseResponseDTO;
import com.tradehero.th.api.discussion.key.MessageHeaderId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.persistence.home.HomeContentCacheRx;
import com.tradehero.th.persistence.message.MessageHeaderCacheRx;
import com.tradehero.th.persistence.message.MessageHeaderListCacheRx;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import android.support.annotation.NonNull;

public class DTOProcessorMessageDeleted extends DTOProcessorMessageRead
{
    @NonNull private final MessageHeaderListCacheRx messageHeaderListCache;
    @NonNull private final MessageHeaderId messageHeaderId;

    //<editor-fold desc="Constructors">
    public DTOProcessorMessageDeleted(
            @NonNull MessageHeaderCacheRx messageHeaderCache,
            @NonNull UserProfileCacheRx userProfileCache,
            @NonNull HomeContentCacheRx homeContentCache,
            @NonNull MessageHeaderListCacheRx messageHeaderListCache,
            @NonNull MessageHeaderId messageHeaderId,
            @NonNull UserBaseKey readerId)
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
