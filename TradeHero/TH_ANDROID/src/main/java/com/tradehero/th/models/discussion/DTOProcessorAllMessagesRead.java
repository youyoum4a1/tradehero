package com.tradehero.th.models.discussion;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.tradehero.th.api.BaseResponseDTO;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.models.ThroughDTOProcessor;
import com.tradehero.th.persistence.message.MessageHeaderCacheRx;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import timber.log.Timber;

public class DTOProcessorAllMessagesRead extends ThroughDTOProcessor<BaseResponseDTO>
{
    @NonNull private final MessageHeaderCacheRx messageHeaderCache;
    @NonNull private final UserProfileCacheRx userProfileCache;
    @Nullable private UserBaseKey readerId;

    //<editor-fold desc="Constructors">
    public DTOProcessorAllMessagesRead(
            @NonNull MessageHeaderCacheRx messageHeaderCache,
            @NonNull UserProfileCacheRx userProfileCache,
            @Nullable UserBaseKey readerId)
    {
        this.messageHeaderCache = messageHeaderCache;
        this.userProfileCache = userProfileCache;
        this.readerId = readerId;
    }
    //</editor-fold>

    @Override public BaseResponseDTO process(BaseResponseDTO value)
    {
        Timber.d("DTOProcessAllMessageRead: process");
        if (readerId != null)
        {
            UserProfileDTO cachedProfile = userProfileCache.getCachedValue(readerId);
            if (cachedProfile != null)
            {
                cachedProfile.unreadMessageThreadsCount = 0;
                userProfileCache.onNext(readerId, cachedProfile);
            }
        }
        messageHeaderCache.setUnreadAll(false);
        return value;
    }
}
