package com.tradehero.th.models.discussion;

import com.tradehero.th.api.discussion.MessageHeaderDTO;
import com.tradehero.th.api.pagination.ReadablePaginatedDTO;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.models.DTOProcessor;
import com.tradehero.th.persistence.user.UserProfileCache;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DTOProcessorReadablePaginatedMessageReceived<
        MessageHeaderDTOType extends MessageHeaderDTO,
        ReadableType extends ReadablePaginatedDTO<MessageHeaderDTOType>>
     implements DTOProcessor<ReadableType>
{
    @NotNull private final UserProfileCache userProfileCache;
    @Nullable private final UserBaseKey readerUserId;

    //<editor-fold desc="Constructors">
    public DTOProcessorReadablePaginatedMessageReceived(
            @NotNull UserProfileCache userProfileCache,
            @Nullable UserBaseKey readerUserId)
    {
        this.userProfileCache = userProfileCache;
        this.readerUserId = readerUserId;
    }
    //</editor-fold>

    @Override @NotNull public ReadableType process(@NotNull ReadableType value)
    {
        if (readerUserId != null)
        {
            UserProfileDTO cachedProfile = userProfileCache.get(readerUserId);
            if (cachedProfile != null)
            {
                cachedProfile.unreadMessageThreadsCount = value.unread;
            }
        }
        return value;
    }
}
