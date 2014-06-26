package com.tradehero.th.models.user;

import com.tradehero.th.api.system.SystemStatusDTO;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserLoginDTO;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.models.DTOProcessor;
import com.tradehero.th.persistence.DTOCacheUtil;
import com.tradehero.th.persistence.system.SystemStatusCache;
import com.tradehero.th.persistence.user.UserProfileCache;
import org.jetbrains.annotations.NotNull;

public class DTOProcessorUserLogin implements DTOProcessor<UserLoginDTO>
{
    @NotNull private final SystemStatusCache systemStatusCache;
    @NotNull private final DTOProcessorSignInUpUserProfile processorSignInUp;

    //<editor-fold desc="Constructors">
    public DTOProcessorUserLogin(
            @NotNull SystemStatusCache systemStatusCache,
            @NotNull UserProfileCache userProfileCache,
            @NotNull CurrentUserId currentUserId,
            @NotNull DTOCacheUtil dtoCacheUtil)
    {
        this.systemStatusCache = systemStatusCache;
        this.processorSignInUp = new DTOProcessorSignInUpUserProfile(
                userProfileCache,
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
                value.profileDTO = processorSignInUp.process(profile);

                UserBaseKey userKey = profile.getBaseKey();
                if (value.systemStatusDTO == null)
                {
                    value.systemStatusDTO = new SystemStatusDTO();
                }
                systemStatusCache.put(userKey, value.systemStatusDTO);
            }
        }
        return value;
    }
}
