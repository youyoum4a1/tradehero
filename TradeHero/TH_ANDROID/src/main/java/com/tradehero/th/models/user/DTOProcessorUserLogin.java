package com.tradehero.th.models.user;

import com.tradehero.th.api.system.SystemStatusDTO;
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
    @NotNull private final UserProfileCache userProfileCache;
    @NotNull private final SystemStatusCache systemStatusCache;
    @NotNull private final DTOCacheUtil dtoCacheUtil;

    //<editor-fold desc="Constructors">
    public DTOProcessorUserLogin(
            @NotNull UserProfileCache userProfileCache,
            @NotNull SystemStatusCache systemStatusCache,
            @NotNull DTOCacheUtil dtoCacheUtil)
    {
        this.userProfileCache = userProfileCache;
        this.systemStatusCache = systemStatusCache;
        this.dtoCacheUtil = dtoCacheUtil;
    }
    //</editor-fold>

    @Override public UserLoginDTO process(UserLoginDTO value)
    {
        if (value != null)
        {
            UserProfileDTO profile = value.profileDTO;
            if (profile != null)
            {
                UserBaseKey userKey = profile.getBaseKey();
                userProfileCache.put(userKey, profile);
                if (value.systemStatusDTO == null)
                {
                    value.systemStatusDTO = new SystemStatusDTO();
                }
                systemStatusCache.put(userKey, value.systemStatusDTO);
            }
        }
        dtoCacheUtil.prefetchesUponLogin(value);
        return value;
    }
}
