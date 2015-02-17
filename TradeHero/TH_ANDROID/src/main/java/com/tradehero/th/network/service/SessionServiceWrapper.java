package com.tradehero.th.network.service;

import android.app.NotificationManager;
import android.content.Context;
import android.support.annotation.NonNull;
import com.tradehero.common.persistence.prefs.StringPreference;
import com.tradehero.th.api.BaseResponseDTO;
import com.tradehero.th.api.system.SystemStatusDTO;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.LoginSignUpFormDTO;
import com.tradehero.th.api.users.UserLoginDTO;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.models.user.DTOProcessorLogout;
import com.tradehero.th.models.user.DTOProcessorUpdateUserProfile;
import com.tradehero.th.models.user.DTOProcessorUserLogin;
import com.tradehero.th.persistence.DTOCacheUtilImpl;
import com.tradehero.th.persistence.home.HomeContentCacheRx;
import com.tradehero.th.persistence.prefs.SavedPushDeviceIdentifier;
import com.tradehero.th.persistence.system.SystemStatusCache;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import dagger.Lazy;
import javax.inject.Inject;
import javax.inject.Singleton;
import rx.Observable;
import rx.functions.Func1;
import rx.functions.Func2;
import timber.log.Timber;

@Singleton public class SessionServiceWrapper
{
    @NonNull private final CurrentUserId currentUserId;
    @NonNull private final SessionServiceRx sessionServiceRx;
    @NonNull private final UserProfileCacheRx userProfileCache;
    @NonNull private final DTOCacheUtilImpl dtoCacheUtil;
    @NonNull private final Context context;
    @NonNull private final StringPreference savedPushDeviceIdentifier;
    @NonNull private final Lazy<SystemStatusCache> systemStatusCache;
    @NonNull private final Lazy<HomeContentCacheRx> homeContentCache;

    //<editor-fold desc="Constructors">
    @Inject public SessionServiceWrapper(
            @NonNull CurrentUserId currentUserId,
            @NonNull SessionServiceRx sessionServiceRx,
            @NonNull UserProfileCacheRx userProfileCache,
            @NonNull DTOCacheUtilImpl dtoCacheUtil,
            @NonNull Context context,
            @NonNull @SavedPushDeviceIdentifier StringPreference savedPushDeviceIdentifier,
            @NonNull Lazy<SystemStatusCache> systemStatusCache,
            @NonNull Lazy<HomeContentCacheRx> homeContentCache)
    {
        this.currentUserId = currentUserId;
        this.sessionServiceRx = sessionServiceRx;
        this.userProfileCache = userProfileCache;
        this.dtoCacheUtil = dtoCacheUtil;
        this.context = context;
        this.savedPushDeviceIdentifier = savedPushDeviceIdentifier;
        this.systemStatusCache = systemStatusCache;
        this.homeContentCache = homeContentCache;
    }
    //</editor-fold>

    //<editor-fold desc="Get System Status">
    @NonNull public Observable<SystemStatusDTO> getSystemStatusRx()
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
    @NonNull protected DTOProcessorUserLogin createUserLoginProcessor()
    {
        return new DTOProcessorUserLogin(
                systemStatusCache.get(),
                userProfileCache,
                homeContentCache.get(),
                currentUserId,
                dtoCacheUtil);
    }

    @NonNull public Observable<UserLoginDTO> loginRx(
            @NonNull String authorization,
            @NonNull LoginSignUpFormDTO loginFormDTO)
    {
        return sessionServiceRx.login(authorization, loginFormDTO)
                .map(createUserLoginProcessor());
    }
    //</editor-fold>

    //<editor-fold desc="Login and social register">
    @NonNull public Observable<UserLoginDTO> signupAndLoginRx(@NonNull String authorizationHeader, @NonNull LoginSignUpFormDTO loginSignUpFormDTO)
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

        return userLoginDTOObservable.map(createUserLoginProcessor());
    }

    @NonNull public Observable<UserLoginDTO> signUpAndLoginOrUpdateTokensRx(
            @NonNull String authorizationHeader,
            @NonNull final LoginSignUpFormDTO loginSignUpFormDTO)
    {
        return signupAndLoginRx(
                authorizationHeader, loginSignUpFormDTO)
                .retry(new Func2<Integer, Throwable, Boolean>()
                {
                    @Override public Boolean call(Integer integer, Throwable throwable)
                    {
                        THException thException = new THException(throwable);
                        if (thException.getCode() == THException.ExceptionCode.RenewSocialToken)
                        {
                            try
                            {
                                SessionServiceWrapper.this.updateAuthorizationTokensRx(loginSignUpFormDTO).subscribe();
                                return true;
                            } catch (Exception ignored)
                            {
                                return false;
                            }
                        }
                        return false;
                    }
                });
    }
    //</editor-fold>

    //<editor-fold desc="Logout">
    @NonNull public Observable<UserProfileDTO> logoutRx()
    {
        return sessionServiceRx.logout()
                .doOnNext(new DTOProcessorLogout(
                        dtoCacheUtil,
                        (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)));
    }
    //</editor-fold>

    //<editor-fold desc="Update Device">
    @NonNull public Observable<UserProfileDTO> updateDeviceRx()
    {
        return sessionServiceRx.updateDevice(savedPushDeviceIdentifier.get())
                .map(new DTOProcessorUpdateUserProfile(userProfileCache, homeContentCache.get()));
    }
    //</editor-fold>

    //<editor-fold desc="Update Authorization Tokens">
    @NonNull public Observable<BaseResponseDTO> updateAuthorizationTokensRx(@NonNull LoginSignUpFormDTO userFormDTO)
    {
        return sessionServiceRx.updateAuthorizationTokens(userFormDTO.authData.getTHToken(), userFormDTO);
    }
    //</editor-fold>
}
