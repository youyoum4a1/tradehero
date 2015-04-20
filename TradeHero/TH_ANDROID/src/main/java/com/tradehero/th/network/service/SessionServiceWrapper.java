package com.tradehero.th.network.service;

import android.content.Context;
import com.tradehero.common.persistence.prefs.StringPreference;
import com.tradehero.th.api.users.*;
import com.tradehero.th.models.DTOProcessor;
import com.tradehero.th.models.user.DTOProcessorUpdateUserProfile;
import com.tradehero.th.models.user.DTOProcessorUserLogin;
import com.tradehero.th.network.retrofit.BaseMiddleCallback;
import com.tradehero.th.network.retrofit.MiddleCallback;
import com.tradehero.th.persistence.DTOCacheUtil;
import com.tradehero.th.persistence.prefs.SavedPushDeviceIdentifier;
import com.tradehero.th.persistence.system.SystemStatusCache;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.utils.Constants;
import dagger.Lazy;
import org.jetbrains.annotations.NotNull;
import retrofit.Callback;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton public class SessionServiceWrapper
{
    @NotNull private final CurrentUserId currentUserId;
    @NotNull private final SessionService sessionService;
    @NotNull private final SessionServiceAsync sessionServiceAsync;
    @NotNull private final UserProfileCache userProfileCache;
    @NotNull private final DTOCacheUtil dtoCacheUtil;
    @NotNull private final Context context;
    @NotNull private final StringPreference savedPushDeviceIdentifier;
    @NotNull private final Lazy<SystemStatusCache> systemStatusCache;

    //<editor-fold desc="Constructors">
    @Inject public SessionServiceWrapper(
            @NotNull CurrentUserId currentUserId,
            @NotNull SessionService sessionService,
            @NotNull SessionServiceAsync sessionServiceAsync,
            @NotNull UserProfileCache userProfileCache,
            @NotNull DTOCacheUtil dtoCacheUtil,
            @NotNull Context context,
            @NotNull @SavedPushDeviceIdentifier StringPreference savedPushDeviceIdentifier,
            @NotNull Lazy<SystemStatusCache> systemStatusCache)
    {
        this.currentUserId = currentUserId;
        this.sessionService = sessionService;
        this.sessionServiceAsync = sessionServiceAsync;
        this.userProfileCache = userProfileCache;
        this.dtoCacheUtil = dtoCacheUtil;
        this.context = context;
        this.savedPushDeviceIdentifier = savedPushDeviceIdentifier;
        this.systemStatusCache = systemStatusCache;
    }
    //</editor-fold>

    //<editor-fold desc="DTO Processors">
    protected DTOProcessor<UserLoginDTO> createUserLoginProcessor()
    {
        return new DTOProcessorUserLogin(
                systemStatusCache.get(),
                userProfileCache,
                currentUserId,
                dtoCacheUtil);
    }

    protected DTOProcessor<UserProfileDTO> createUpdateDeviceProcessor()
    {
        return new DTOProcessorUpdateUserProfile(userProfileCache);
    }


    //<editor-fold desc="Login">
    public UserLoginDTO login(String authorization, LoginFormDTO loginFormDTO)
    {
        loginFormDTO.channelType = Constants.TAP_STREAM_TYPE.type;
        return createUserLoginProcessor().process(sessionService.login(authorization, loginFormDTO));
    }

    public MiddleCallback<UserLoginDTO> login(String authorization, LoginFormDTO loginFormDTO, Callback<UserLoginDTO> callback)
    {
        loginFormDTO.channelType = Constants.TAP_STREAM_TYPE.type;
        MiddleCallback<UserLoginDTO> middleCallback = new BaseMiddleCallback<>(callback, createUserLoginProcessor());
        sessionServiceAsync.login(authorization, loginFormDTO, middleCallback);
        return middleCallback;
    }
    //</editor-fold>


    public MiddleCallback<UserLoginDTO> signupAndLogin(String authorization, LoginSignUpFormDTO loginSignUpFormDTO, Callback<UserLoginDTO> callback)
    {
        loginSignUpFormDTO.channelType = Constants.TAP_STREAM_TYPE.type;
        MiddleCallback<UserLoginDTO> middleCallback = new BaseMiddleCallback<>(callback, createUserLoginProcessor());
        sessionServiceAsync.signupAndLogin(authorization, loginSignUpFormDTO, middleCallback);
        return middleCallback;
    }
    //</editor-fold>

    //<editor-fold desc="Update Device">

    public void updateDevice(String deviceToken, Callback callback){
        sessionServiceAsync.updateDevice(deviceToken, callback);
    }
    //</editor-fold>
}
