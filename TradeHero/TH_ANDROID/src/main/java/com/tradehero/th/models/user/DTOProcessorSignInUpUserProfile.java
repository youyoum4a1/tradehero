package com.tradehero.th.models.user;

import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.persistence.DTOCacheUtilImpl;
import com.tradehero.th.persistence.home.HomeContentCacheRx;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import android.support.annotation.NonNull;

public class DTOProcessorSignInUpUserProfile extends DTOProcessorUpdateUserProfile
{
    @NonNull private final CurrentUserId currentUserId;
    @NonNull private final DTOCacheUtilImpl dtoCacheUtil;

    //<editor-fold desc="Constructors">
    public DTOProcessorSignInUpUserProfile(
            @NonNull UserProfileCacheRx userProfileCache,
            @NonNull HomeContentCacheRx homeContentCache,
            @NonNull CurrentUserId currentUserId,
            @NonNull DTOCacheUtilImpl dtoCacheUtil)
    {
        super(userProfileCache, homeContentCache);
        this.currentUserId = currentUserId;
        this.dtoCacheUtil = dtoCacheUtil;
    }
    //</editor-fold>

    @Override public UserProfileDTO process(@NonNull UserProfileDTO userProfileDTO)
    {
        UserProfileDTO processed = super.process(userProfileDTO);
        currentUserId.set(userProfileDTO.id);
        dtoCacheUtil.prefetchesUponLogin(userProfileDTO);
        return processed;
    }
}
