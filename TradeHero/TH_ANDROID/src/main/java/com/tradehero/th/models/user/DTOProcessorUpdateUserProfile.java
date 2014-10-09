package com.tradehero.th.models.user;

import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.models.DTOProcessor;
import com.tradehero.th.persistence.user.UserProfileCache;
import org.jetbrains.annotations.NotNull;
import rx.functions.Action1;

public class DTOProcessorUpdateUserProfile implements DTOProcessor<UserProfileDTO>
    , Action1<UserProfileDTO>
{
    @NotNull protected final UserProfileCache userProfileCache;

    //<editor-fold desc="Constructors">
    public DTOProcessorUpdateUserProfile(@NotNull UserProfileCache userProfileCache)
    {
        this.userProfileCache = userProfileCache;
    }
    //</editor-fold>

    @Override public UserProfileDTO process(@NotNull UserProfileDTO userProfileDTO)
    {
        userProfileCache.put(userProfileDTO.getBaseKey(), userProfileDTO);
        return userProfileDTO;
    }

    @Override public void call(@NotNull UserProfileDTO userProfileDTO)
    {
        process(userProfileDTO);
    }
}
