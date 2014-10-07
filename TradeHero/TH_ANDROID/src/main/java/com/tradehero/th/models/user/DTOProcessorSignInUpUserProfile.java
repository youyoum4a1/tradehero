package com.tradehero.th.models.user;

import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.persistence.DTOCacheUtil;
import com.tradehero.th.persistence.home.HomeContentCache;
import com.tradehero.th.persistence.user.UserProfileCache;
import org.jetbrains.annotations.NotNull;

public class DTOProcessorSignInUpUserProfile extends DTOProcessorUpdateUserProfile
{
    @NotNull private final CurrentUserId currentUserId;
    @NotNull private final DTOCacheUtil dtoCacheUtil;

    //<editor-fold desc="Constructors">
    public DTOProcessorSignInUpUserProfile(
            @NotNull UserProfileCache userProfileCache,
            @NotNull HomeContentCache homeContentCache,
            @NotNull CurrentUserId currentUserId,
            @NotNull DTOCacheUtil dtoCacheUtil)
    {
        super(userProfileCache, homeContentCache);
        this.currentUserId = currentUserId;
        this.dtoCacheUtil = dtoCacheUtil;
    }
    //</editor-fold>

    @Override public UserProfileDTO process(@NotNull UserProfileDTO userProfileDTO)
    {
        UserProfileDTO processed = super.process(userProfileDTO);
        currentUserId.set(userProfileDTO.id);
        dtoCacheUtil.prefetchesUponLogin(userProfileDTO);
        return processed;
    }
}
