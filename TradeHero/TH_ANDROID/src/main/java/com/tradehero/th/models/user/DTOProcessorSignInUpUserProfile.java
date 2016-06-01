package com.ayondo.academy.models.user;

import android.content.Context;
import android.support.annotation.NonNull;
import com.tradehero.common.persistence.DTOCacheUtilRx;
import com.tradehero.common.persistence.prefs.BooleanPreference;
import com.ayondo.academy.api.users.CurrentUserId;
import com.ayondo.academy.api.users.UserProfileDTO;
import com.ayondo.academy.auth.AuthData;
import com.ayondo.academy.auth.AuthDataUtil;
import com.ayondo.academy.persistence.user.UserProfileCacheRx;

public class DTOProcessorSignInUpUserProfile extends DTOProcessorUpdateUserProfile
{
    @NonNull private final Context context;
    @NonNull private final CurrentUserId currentUserId;
    @NonNull private final AuthData authData;
    @NonNull private final DTOCacheUtilRx dtoCacheUtil;
    @NonNull private final BooleanPreference isOnBoardShown;

    //<editor-fold desc="Constructors">
    public DTOProcessorSignInUpUserProfile(
            @NonNull Context context,
            @NonNull UserProfileCacheRx userProfileCache,
            @NonNull CurrentUserId currentUserId,
            @NonNull AuthData authData,
            @NonNull DTOCacheUtilRx dtoCacheUtil,
            @NonNull BooleanPreference isOnBoardShown)
    {
        super(userProfileCache);
        this.context = context;
        this.currentUserId = currentUserId;
        this.authData = authData;
        this.dtoCacheUtil = dtoCacheUtil;
        this.isOnBoardShown = isOnBoardShown;
    }
    //</editor-fold>

    @Override public UserProfileDTO process(@NonNull UserProfileDTO userProfileDTO)
    {
        UserProfileDTO processed = super.process(userProfileDTO);
        AuthDataUtil.saveAccount(context, authData, userProfileDTO.email);
        currentUserId.set(userProfileDTO.id);
        dtoCacheUtil.prefetchesUponLogin(userProfileDTO);
        if (userProfileDTO.firstTimeLogin)
        {
            isOnBoardShown.set(false);
        }
        return processed;
    }
}
