package com.tradehero.th.models.discussion;

import com.tradehero.th.api.BaseResponseDTO;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.models.DTOProcessor;
import com.tradehero.th.persistence.home.HomeContentCacheRx;
import com.tradehero.th.persistence.message.MessageHeaderCache;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import timber.log.Timber;

public class DTOProcessorAllMessagesRead implements DTOProcessor<BaseResponseDTO>
{
    @NotNull private final MessageHeaderCache messageHeaderCache;
    @NotNull private final UserProfileCacheRx userProfileCache;
    @NotNull private final HomeContentCacheRx homeContentCache;
    @Nullable private UserBaseKey readerId;

    //<editor-fold desc="Constructors">
    public DTOProcessorAllMessagesRead(
            @NotNull MessageHeaderCache messageHeaderCache,
            @NotNull UserProfileCacheRx userProfileCache,
            @NotNull HomeContentCacheRx homeContentCache,
            @Nullable UserBaseKey readerId)
    {
        this.messageHeaderCache = messageHeaderCache;
        this.userProfileCache = userProfileCache;
        this.homeContentCache = homeContentCache;
        this.readerId = readerId;
    }
    //</editor-fold>

    @Override public BaseResponseDTO process(BaseResponseDTO value)
    {
        Timber.d("DTOProcessAllMessageRead: process");
        if (readerId != null)
        {
            userProfileCache.get(readerId);
            homeContentCache.invalidate(readerId);
        }
        messageHeaderCache.invalidateAll();
        return value;
    }
}
