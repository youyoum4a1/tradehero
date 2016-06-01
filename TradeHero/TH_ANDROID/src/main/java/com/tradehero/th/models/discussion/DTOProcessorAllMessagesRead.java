package com.ayondo.academy.models.discussion;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.ayondo.academy.api.BaseResponseDTO;
import com.ayondo.academy.api.users.UserBaseKey;
import com.ayondo.academy.api.users.UserProfileDTO;
import com.ayondo.academy.models.ThroughDTOProcessor;
import com.ayondo.academy.persistence.message.MessageHeaderCacheRx;
import com.ayondo.academy.persistence.user.UserProfileCacheRx;
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
