package com.androidth.general.models.user;

import android.content.Context;
import android.support.annotation.NonNull;
import com.androidth.general.common.persistence.DTOCacheUtilRx;
import com.androidth.general.common.persistence.prefs.BooleanPreference;
import com.androidth.general.api.system.SystemStatusDTO;
import com.androidth.general.api.system.SystemStatusKey;
import com.androidth.general.api.users.CurrentUserId;
import com.androidth.general.api.users.UserLoginDTO;
import com.androidth.general.api.users.UserProfileDTO;
import com.androidth.general.auth.AuthData;
import com.androidth.general.auth.AuthDataUtil;
import com.androidth.general.models.ThroughDTOProcessor;
import com.androidth.general.persistence.system.SystemStatusCache;
import com.androidth.general.persistence.user.UserProfileCacheRx;

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
