package com.androidth.general.models.discussion;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.androidth.general.api.BaseResponseDTO;
import com.androidth.general.api.users.UserBaseKey;
import com.androidth.general.api.users.UserProfileDTO;
import com.androidth.general.models.ThroughDTOProcessor;
import com.androidth.general.persistence.message.MessageHeaderCacheRx;
import com.androidth.general.persistence.user.UserProfileCacheRx;
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
