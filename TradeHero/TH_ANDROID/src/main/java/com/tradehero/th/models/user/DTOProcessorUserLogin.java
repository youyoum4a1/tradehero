package com.ayondo.academy.models.user;

import android.content.Context;
import android.support.annotation.NonNull;
import com.tradehero.common.persistence.DTOCacheUtilRx;
import com.tradehero.common.persistence.prefs.BooleanPreference;
import com.ayondo.academy.api.system.SystemStatusDTO;
import com.ayondo.academy.api.system.SystemStatusKey;
import com.ayondo.academy.api.users.CurrentUserId;
import com.ayondo.academy.api.users.UserLoginDTO;
import com.ayondo.academy.api.users.UserProfileDTO;
import com.ayondo.academy.auth.AuthData;
import com.ayondo.academy.auth.AuthDataUtil;
import com.ayondo.academy.models.ThroughDTOProcessor;
import com.ayondo.academy.persistence.system.SystemStatusCache;
import com.ayondo.academy.persistence.user.UserProfileCacheRx;

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
            @NonNull DTOCacheUtilRx dtoCacheUtil,
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
