package com.tradehero.th.models.discussion;

import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.models.DTOProcessor;
import com.tradehero.th.persistence.user.UserProfileCache;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import retrofit.client.Response;
import timber.log.Timber;

public class DTOProcessorAllMessagesRead implements DTOProcessor<Response>
{
    @NotNull private final UserProfileCache userProfileCache;
    @NotNull private UserBaseKey readerId;

    //<editor-fold desc="Constructors">
    public DTOProcessorAllMessagesRead(
            @NotNull UserProfileCache userProfileCache,
            @Nullable UserBaseKey readerId)
    {
        this.userProfileCache = userProfileCache;
        this.readerId = readerId;
    }
    //</editor-fold>

    @Override public Response process(Response value)
    {
        Timber.d("DTOProcessAllMessageRead: process");
        if (readerId != null)
        {
            userProfileCache.getOrFetchAsync(readerId, true);
        }
        return value;
    }
}
