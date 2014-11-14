package com.tradehero.th.models.user;

import android.support.annotation.NonNull;
import com.tradehero.th.api.system.SystemStatusDTO;
import com.tradehero.th.api.system.SystemStatusKey;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserLoginDTO;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.models.ThroughDTOProcessor;
import com.tradehero.th.persistence.DTOCacheUtilImpl;
import com.tradehero.th.persistence.home.HomeContentCacheRx;
import com.tradehero.th.persistence.system.SystemStatusCache;
import com.tradehero.th.persistence.user.UserProfileCacheRx;

public class DTOProcessorUserLogin extends ThroughDTOProcessor<UserLoginDTO>
{
    @NonNull private final SystemStatusCache systemStatusCache;
    @NonNull private final CurrentUserId currentUserId;
    @NonNull private final DTOProcessorSignInUpUserProfile processorSignInUp;

    //<editor-fold desc="Constructors">
    public DTOProcessorUserLogin(
            @NonNull SystemStatusCache systemStatusCache,
            @NonNull UserProfileCacheRx userProfileCache,
            @NonNull HomeContentCacheRx homeContentCache,
            @NonNull CurrentUserId currentUserId,
            @NonNull DTOCacheUtilImpl dtoCacheUtil)
    {
        this.systemStatusCache = systemStatusCache;
        this.currentUserId = currentUserId;
        this.processorSignInUp = new DTOProcessorSignInUpUserProfile(
                userProfileCache,
                homeContentCache,
                currentUserId,
                dtoCacheUtil);
    }
    //</editor-fold>

    @Override public UserLoginDTO process(UserLoginDTO value)
    {
        if (value != null)
        {
            UserProfileDTO profile = value.profileDTO;
            if (profile != null)
            {
                currentUserId.set(profile.id);
                value.profileDTO = processorSignInUp.process(profile);

                UserBaseKey userKey = profile.getBaseKey();
                if (value.systemStatusDTO == null)
                {
                    value.systemStatusDTO = new SystemStatusDTO();
                }
                systemStatusCache.onNext(new SystemStatusKey(), value.systemStatusDTO);
            }
        }
        return value;
    }
}
