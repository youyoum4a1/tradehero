package com.tradehero.th.network.service;

import android.app.NotificationManager;
import android.content.Context;
import com.tradehero.common.persistence.prefs.StringPreference;
import com.tradehero.th.api.BaseResponseDTO;
import com.tradehero.th.api.system.SystemStatusDTO;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.LoginSignUpFormDTO;
import com.tradehero.th.api.users.UserLoginDTO;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.models.DTOProcessor;
import com.tradehero.th.models.user.DTOProcessorLogout;
import com.tradehero.th.models.user.DTOProcessorUpdateUserProfile;
import com.tradehero.th.models.user.DTOProcessorUserLogin;
import com.tradehero.th.network.retrofit.BaseMiddleCallback;
import com.tradehero.th.network.retrofit.MiddleCallback;
import com.tradehero.th.persistence.DTOCacheUtilImpl;
import com.tradehero.th.persistence.home.HomeContentCacheRx;
import com.tradehero.th.persistence.prefs.SavedPushDeviceIdentifier;
import com.tradehero.th.persistence.system.SystemStatusCache;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import dagger.Lazy;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import retrofit.Callback;
import rx.Observable;
import rx.functions.Func1;
import timber.log.Timber;

@Singleton public class SessionServiceWrapper
{
    @NotNull private final CurrentUserId currentUserId;
    @NotNull private final SessionService sessionService;
    @NotNull private final SessionServiceAsync sessionServiceAsync;
    @NotNull private final SessionServiceRx sessionServiceRx;
    @NotNull private final UserProfileCacheRx userProfileCache;
    @NotNull private final DTOCacheUtilImpl dtoCacheUtil;
    @NotNull private final Context context;
    @NotNull private final StringPreference savedPushDeviceIdentifier;
    @NotNull private final Lazy<SystemStatusCache> systemStatusCache;
    @NotNull private final Lazy<HomeContentCacheRx> homeContentCache;

    //<editor-fold desc="Constructors">
    @Inject public SessionServiceWrapper(
            @NotNull CurrentUserId currentUserId,
            @NotNull SessionService sessionService,
            @NotNull SessionServiceAsync sessionServiceAsync,
            @NotNull SessionServiceRx sessionServiceRx,
            @NotNull UserProfileCacheRx userProfileCache,
            @NotNull DTOCacheUtilImpl dtoCacheUtil,
            @NotNull Context context,
            @NotNull @SavedPushDeviceIdentifier StringPreference savedPushDeviceIdentifier,
            @NotNull Lazy<SystemStatusCache> systemStatusCache,
            @NotNull Lazy<HomeContentCacheRx> homeContentCache)
    {
        this.currentUserId = currentUserId;
        this.sessionService = sessionService;
        this.sessionServiceAsync = sessionServiceAsync;
        this.sessionServiceRx = sessionServiceRx;
        this.userProfileCache = userProfileCache;
        this.dtoCacheUtil = dtoCacheUtil;
        this.context = context;
        this.savedPushDeviceIdentifier = savedPushDeviceIdentifier;
        this.systemStatusCache = systemStatusCache;
        this.homeContentCache = homeContentCache;
    }
    //</editor-fold>

    //<editor-fold desc="DTO Processors">
    @NotNull protected DTOProcessorUserLogin createUserLoginProcessor()
    {
        return new DTOProcessorUserLogin(
                systemStatusCache.get(),
                userProfileCache,
                homeContentCache.get(),
                currentUserId,
                dtoCacheUtil);
    }

    @NotNull protected DTOProcessor<UserProfileDTO> createUpdateDeviceProcessor()
    {
        return new DTOProcessorUpdateUserProfile(userProfileCache, homeContentCache.get());
    }

    @NotNull protected DTOProcessorLogout createLogoutProcessor()
    {
        return new DTOProcessorLogout(
                dtoCacheUtil, dtoCacheUtil,
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE));
    }
    //</editor-fold>

    //<editor-fold desc="Get System Status">
    @NotNull public Observable<SystemStatusDTO> getSystemStatusRx()
    {
        return sessionServiceRx.getSystemStatus()
                .onErrorReturn(new Func1<Throwable, SystemStatusDTO>()
                {
                    @Override public SystemStatusDTO call(Throwable throwable)
                    {
                        Timber.e(throwable, "When requesting for systemStatus");
                        return new SystemStatusDTO();
                    }
                });
    }
    //</editor-fold>

    //<editor-fold desc="Login">
    @NotNull public UserLoginDTO login(
            @NotNull String authorization,
            @NotNull LoginSignUpFormDTO loginFormDTO)
    {
        return createUserLoginProcessor().process(sessionService.login(authorization, loginFormDTO));
    }

    @NotNull public Observable<UserLoginDTO> loginRx(
            @NotNull String authorization,
            @NotNull LoginSignUpFormDTO loginFormDTO)
    {
        return sessionServiceRx.login(authorization, loginFormDTO);
    }

    @NotNull public MiddleCallback<UserLoginDTO> login(
            @NotNull String authorization,
            @NotNull LoginSignUpFormDTO loginFormDTO,
            @Nullable Callback<UserLoginDTO> callback)
    {
        MiddleCallback<UserLoginDTO> middleCallback = new BaseMiddleCallback<>(callback, createUserLoginProcessor());
        sessionServiceAsync.login(authorization, loginFormDTO, middleCallback);
        return middleCallback;
    }
    //</editor-fold>

    //<editor-fold desc="Login and social register">
    @NotNull public Observable<UserLoginDTO> signupAndLoginRx(@NotNull String authorizationHeader, @NotNull LoginSignUpFormDTO loginSignUpFormDTO)
    {
        Observable<UserLoginDTO> userLoginDTOObservable;
        switch (loginSignUpFormDTO.authData.socialNetworkEnum)
        {
            case TH:
                userLoginDTOObservable = sessionServiceRx.login(authorizationHeader, loginSignUpFormDTO);
                break;
            default:
                userLoginDTOObservable = sessionServiceRx.signupAndLogin(authorizationHeader, loginSignUpFormDTO);
        }

        return userLoginDTOObservable.doOnNext(createUserLoginProcessor());
    }
    //</editor-fold>

    //<editor-fold desc="Logout">
    @NotNull public UserProfileDTO logout()
    {
        return createLogoutProcessor().process(sessionService.logout());
    }

    @NotNull public MiddleCallback<UserProfileDTO> logout(@Nullable Callback<UserProfileDTO> callback)
    {
        MiddleCallback<UserProfileDTO> middleCallback = new BaseMiddleCallback<>(callback, createLogoutProcessor());
        sessionServiceAsync.logout(middleCallback);
        return middleCallback;
    }

    @NotNull public Observable<UserProfileDTO> logoutRx()
    {
        return sessionServiceRx.logout()
                .doOnNext(createLogoutProcessor());
    }
    //</editor-fold>

    //<editor-fold desc="Update Device">
    @NotNull public MiddleCallback<UserProfileDTO> updateDevice(@Nullable Callback<UserProfileDTO> callback)
    {
        MiddleCallback<UserProfileDTO> middleCallback = new BaseMiddleCallback<>(callback, createUpdateDeviceProcessor());
        sessionServiceAsync.updateDevice(savedPushDeviceIdentifier.get(), middleCallback);
        return middleCallback;
    }

    @NotNull public Observable<UserProfileDTO> updateDeviceRx()
    {
        return sessionServiceRx.updateDevice(savedPushDeviceIdentifier.get());
    }
    //</editor-fold>

    //<editor-fold desc="Update Authorization Tokens">
    @NotNull public BaseResponseDTO updateAuthorizationTokens(@NotNull LoginSignUpFormDTO userFormDTO)
    {
        return sessionService.updateAuthorizationTokens(userFormDTO.authData.getTHToken(), userFormDTO);
    }

    @NotNull public Observable<BaseResponseDTO> updateAuthorizationTokensRx(@NotNull LoginSignUpFormDTO userFormDTO)
    {
        return sessionServiceRx.updateAuthorizationTokens(userFormDTO.authData.getTHToken(), userFormDTO);
    }
    //</editor-fold>
}
