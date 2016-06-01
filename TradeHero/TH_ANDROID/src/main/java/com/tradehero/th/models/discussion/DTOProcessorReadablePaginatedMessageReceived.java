package com.ayondo.academy.models.discussion;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.ayondo.academy.api.discussion.MessageHeaderDTO;
import com.ayondo.academy.api.pagination.ReadablePaginatedDTO;
import com.ayondo.academy.api.users.UserBaseKey;
import com.ayondo.academy.api.users.UserProfileDTO;
import com.ayondo.academy.models.ThroughDTOProcessor;
import com.ayondo.academy.persistence.user.UserProfileCacheRx;

public class DTOProcessorReadablePaginatedMessageReceived<
        MessageHeaderDTOType extends MessageHeaderDTO,
        ReadableType extends ReadablePaginatedDTO<MessageHeaderDTOType>>
     extends ThroughDTOProcessor<ReadableType>
{
    @NonNull private final UserProfileCacheRx userProfileCache;
    @Nullable private final UserBaseKey readerUserId;

    //<editor-fold desc="Constructors">
    public DTOProcessorReadablePaginatedMessageReceived(
            @NonNull UserProfileCacheRx userProfileCache,
            @Nullable UserBaseKey readerUserId)
    {
        this.userProfileCache = userProfileCache;
        this.readerUserId = readerUserId;
    }
    //</editor-fold>

    @Override @NonNull public ReadableType process(@NonNull ReadableType value)
    {
        if (readerUserId != null)
        {
            UserProfileDTO cachedProfile = userProfileCache.getCachedValue(readerUserId);
            if (cachedProfile != null)
            {
                cachedProfile.unreadMessageThreadsCount = value.unread;
            }
        }
        return value;
    }
}
