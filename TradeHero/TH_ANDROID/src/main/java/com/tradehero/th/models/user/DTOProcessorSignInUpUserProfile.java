package com.tradehero.th.models.user;

import android.content.Context;
import android.support.annotation.NonNull;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.auth.AuthData;
import com.tradehero.th.auth.AuthDataUtil;
import com.tradehero.th.persistence.DTOCacheUtilImpl;
import com.tradehero.th.persistence.user.UserProfileCacheRx;

public class DTOProcessorSignInUpUserProfile extends DTOProcessorUpdateUserProfile
{
    @NonNull private final Context context;
    @NonNull private final CurrentUserId currentUserId;
    @NonNull private final AuthData authData;
    @NonNull private final DTOCacheUtilImpl dtoCacheUtil;

    //<editor-fold desc="Constructors">
    public DTOProcessorSignInUpUserProfile(
            @NonNull Context context,
            @NonNull UserProfileCacheRx userProfileCache,
            @NonNull CurrentUserId currentUserId,
            @NonNull AuthData authData,
            @NonNull DTOCacheUtilImpl dtoCacheUtil)
    {
        super(userProfileCache);
        this.context = context;
        this.currentUserId = currentUserId;
        this.authData = authData;
        this.dtoCacheUtil = dtoCacheUtil;
    }
    //</editor-fold>

    @Override public UserProfileDTO process(@NonNull UserProfileDTO userProfileDTO)
    {
        UserProfileDTO processed = super.process(userProfileDTO);
        AuthDataUtil.saveAccount(context, authData, userProfileDTO.email);
        currentUserId.set(userProfileDTO.id);
        dtoCacheUtil.prefetchesUponLogin(userProfileDTO);
        return processed;
    }
}
