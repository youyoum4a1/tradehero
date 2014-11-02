package com.tradehero.th.models.user;

import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.persistence.DTOCacheUtilImpl;
import com.tradehero.th.persistence.home.HomeContentCacheRx;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import org.jetbrains.annotations.NotNull;

public class DTOProcessorSignInUpUserProfile extends DTOProcessorUpdateUserProfile
{
    @NotNull private final CurrentUserId currentUserId;
    @NotNull private final DTOCacheUtilImpl dtoCacheUtil;

    //<editor-fold desc="Constructors">
    public DTOProcessorSignInUpUserProfile(
            @NotNull UserProfileCacheRx userProfileCache,
            @NotNull HomeContentCacheRx homeContentCache,
            @NotNull CurrentUserId currentUserId,
            @NotNull DTOCacheUtilImpl dtoCacheUtil)
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
