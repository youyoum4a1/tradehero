package com.tradehero.th.models.user;

import android.content.Context;
import android.support.annotation.NonNull;
import com.tradehero.common.persistence.prefs.BooleanPreference;
import com.tradehero.th.api.system.SystemStatusDTO;
import com.tradehero.th.api.system.SystemStatusKey;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserLoginDTO;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.auth.AuthData;
import com.tradehero.th.auth.AuthDataUtil;
import com.tradehero.th.models.ThroughDTOProcessor;
import com.tradehero.th.persistence.DTOCacheUtilImpl;
import com.tradehero.th.persistence.system.SystemStatusCache;
import com.tradehero.th.persistence.user.UserProfileCacheRx;

public class DTOProcessorUserLogin extends ThroughDTOProcessor<UserLoginDTO>
{
    @NonNull private final AuthData authData;
    @NonNull private final SystemStatusCache systemStatusCache;
    @NonNull private final CurrentUserId currentUserId;
    @NonNull private final Context context;
    @NonNull private final DTOProcessorSignInUpUserProfile processorSignInUp;

    //<editor-fold desc="Constructors">
    public DTOProcessorUserLogin(
            @NonNull AuthData authData,
            @NonNull SystemStatusCache systemStatusCache,
            @NonNull UserProfileCacheRx userProfileCache,
            @NonNull CurrentUserId currentUserId,
            @NonNull Context context,
            @NonNull DTOCacheUtilImpl dtoCacheUtil,
            @NonNull BooleanPreference isOnBoardShown)
    {
        this.authData = authData;
        this.systemStatusCache = systemStatusCache;
        this.currentUserId = currentUserId;
        this.context = context;
        this.processorSignInUp = new DTOProcessorSignInUpUserProfile(
                context,
                userProfileCache,
                currentUserId,
                authData,
                dtoCacheUtil,
                isOnBoardShown);
    }
    //</editor-fold>

    @Override public UserLoginDTO process(UserLoginDTO value)
    {
        if (value != null)
        {
            UserProfileDTO profile = value.profileDTO;
            if (profile != null)
            {
                AuthDataUtil.saveAccount(context, authData, profile.email);
                currentUserId.set(profile.id);
                value.profileDTO = processorSignInUp.process(profile);

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
