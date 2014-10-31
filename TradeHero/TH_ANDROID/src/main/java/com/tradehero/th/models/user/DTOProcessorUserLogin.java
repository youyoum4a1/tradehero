package com.tradehero.th.models.user;

import com.tradehero.th.api.system.SystemStatusDTO;
import com.tradehero.th.api.system.SystemStatusKey;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserLoginDTO;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.models.DTOProcessor;
import com.tradehero.th.persistence.DTOCacheUtilImpl;
import com.tradehero.th.persistence.home.HomeContentCache;
import com.tradehero.th.persistence.system.SystemStatusCache;
import com.tradehero.th.persistence.user.UserProfileCache;
import org.jetbrains.annotations.NotNull;
import rx.functions.Action1;

public class DTOProcessorUserLogin implements DTOProcessor<UserLoginDTO>
    , Action1<UserLoginDTO> // TODO remove when changed DTOProcessor
{
    @NotNull private final SystemStatusCache systemStatusCache;
    @NotNull private final CurrentUserId currentUserId;
    @NotNull private final DTOProcessorSignInUpUserProfile processorSignInUp;

    //<editor-fold desc="Constructors">
    public DTOProcessorUserLogin(
            @NotNull SystemStatusCache systemStatusCache,
            @NotNull UserProfileCache userProfileCache,
            @NotNull HomeContentCache homeContentCache,
            @NotNull CurrentUserId currentUserId,
            @NotNull DTOCacheUtilImpl dtoCacheUtil)
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

    @Override public void call(UserLoginDTO userLoginDTO)
    {
        process(userLoginDTO);
    }
}
