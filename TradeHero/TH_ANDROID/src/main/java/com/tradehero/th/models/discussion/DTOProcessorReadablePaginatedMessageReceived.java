package com.tradehero.th.models.discussion;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.tradehero.th.api.discussion.MessageHeaderDTO;
import com.tradehero.th.api.pagination.ReadablePaginatedDTO;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.models.ThroughDTOProcessor;
import com.tradehero.th.persistence.user.UserProfileCacheRx;

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
            UserProfileDTO cachedProfile = userProfileCache.getValue(readerUserId);
            if (cachedProfile != null)
            {
                cachedProfile.unreadMessageThreadsCount = value.unread;
            }
        }
        return value;
    }
}
