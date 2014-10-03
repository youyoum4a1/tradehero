package com.tradehero.th.models.discussion;

import com.tradehero.th.api.BaseResponseDTO;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.models.DTOProcessor;
import com.tradehero.th.persistence.message.MessageHeaderCache;
import com.tradehero.th.persistence.user.UserProfileCache;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import timber.log.Timber;

public class DTOProcessorAllMessagesRead implements DTOProcessor<BaseResponseDTO>
{
    @NotNull private final MessageHeaderCache messageHeaderCache;
    @NotNull private final UserProfileCache userProfileCache;
    @Nullable private UserBaseKey readerId;

    //<editor-fold desc="Constructors">
    public DTOProcessorAllMessagesRead(
            @NotNull MessageHeaderCache messageHeaderCache,
            @NotNull UserProfileCache userProfileCache,
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
            userProfileCache.getOrFetchAsync(readerId, true);
        }
        messageHeaderCache.invalidateAll();
        return value;
    }
}
