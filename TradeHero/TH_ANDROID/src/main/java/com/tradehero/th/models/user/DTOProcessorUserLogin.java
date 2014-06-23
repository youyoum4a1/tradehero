package com.tradehero.th.models.user;

import com.tradehero.th.api.system.SystemStatusDTO;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserLoginDTO;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.models.DTOProcessor;
import com.tradehero.th.persistence.system.SystemStatusCache;
import org.jetbrains.annotations.NotNull;

public class DTOProcessorUserLogin implements DTOProcessor<UserLoginDTO>
{
    @NotNull private final SystemStatusCache systemStatusCache;

    //<editor-fold desc="Constructors">
    public DTOProcessorUserLogin(
            @NotNull SystemStatusCache systemStatusCache)
    {
        this.systemStatusCache = systemStatusCache;
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
