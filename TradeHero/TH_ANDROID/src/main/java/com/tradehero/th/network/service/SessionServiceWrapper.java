package com.tradehero.th.network.service;

import android.app.NotificationManager;
import android.content.Context;
import com.tradehero.common.persistence.prefs.StringPreference;
import com.tradehero.th.api.users.LoginFormDTO;
import com.tradehero.th.api.users.UserLoginDTO;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.models.DTOProcessor;
import com.tradehero.th.models.user.DTOProcessorLogout;
import com.tradehero.th.models.user.DTOProcessorUpdateUserProfile;
import com.tradehero.th.network.retrofit.BaseMiddleCallback;
import com.tradehero.th.network.retrofit.MiddleCallback;
import com.tradehero.th.persistence.DTOCacheUtil;
import com.tradehero.th.persistence.prefs.SavedPushDeviceIdentifier;
import com.tradehero.th.persistence.user.UserProfileCache;
import javax.inject.Inject;
import javax.inject.Singleton;
import retrofit.Callback;

@Singleton public class SessionServiceWrapper
{
    private final SessionService sessionService;
    private final SessionServiceAsync sessionServiceAsync;
    private final UserProfileCache userProfileCache;
    private final DTOCacheUtil dtoCacheUtil;
    private final Context context;
    private final StringPreference savedPushDeviceIdentifier;

    @Inject public SessionServiceWrapper(
            SessionService sessionService,
            SessionServiceAsync sessionServiceAsync,
            UserProfileCache userProfileCache,
            DTOCacheUtil dtoCacheUtil, Context context,
            @SavedPushDeviceIdentifier StringPreference savedPushDeviceIdentifier)
    {
        this.sessionService = sessionService;
        this.sessionServiceAsync = sessionServiceAsync;
        this.userProfileCache = userProfileCache;
        this.dtoCacheUtil = dtoCacheUtil;
        this.context = context;
        this.savedPushDeviceIdentifier = savedPushDeviceIdentifier;
    }

    //<editor-fold desc="DTO Processors">
    protected DTOProcessor<UserProfileDTO> createUpdateDeviceProcessor()
    {
        return new DTOProcessorUpdateUserProfile(userProfileCache);
    }

    protected DTOProcessor<UserProfileDTO> createLogoutProcessor()
    {
        return new DTOProcessorLogout(
                dtoCacheUtil,
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE));
    }
    //</editor-fold>

    //<editor-fold desc="Login">
    public UserLoginDTO login(String authorization, LoginFormDTO loginFormDTO)
    {
        return sessionService.login(authorization, loginFormDTO);
    }

    public MiddleCallback<UserLoginDTO> login(String authorization, LoginFormDTO loginFormDTO, Callback<UserLoginDTO> callback)
    {
        MiddleCallback<UserLoginDTO> middleCallback = new BaseMiddleCallback<>(callback);
        sessionServiceAsync.login(authorization, loginFormDTO, middleCallback);
        return middleCallback;
    }
    //</editor-fold>

    //<editor-fold desc="Logout">
    public UserProfileDTO logout()
    {
        return createLogoutProcessor().process(sessionService.logout());
    }

    public MiddleCallback<UserProfileDTO> logout(Callback<UserProfileDTO> callback)
    {
        MiddleCallback<UserProfileDTO> middleCallback = new BaseMiddleCallback<>(callback, createLogoutProcessor());
        sessionServiceAsync.logout(middleCallback);
        return middleCallback;
    }
    //</editor-fold>

    //<editor-fold desc="Update Device">

    public MiddleCallback<UserProfileDTO> updateDevice(Callback<UserProfileDTO> callback)
    {
        MiddleCallback<UserProfileDTO> middleCallback = new BaseMiddleCallback<>(callback, createUpdateDeviceProcessor());
        sessionServiceAsync.updateDevice(savedPushDeviceIdentifier.get(), middleCallback);
        return middleCallback;
    }
    //</editor-fold>
}
