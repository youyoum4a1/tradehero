package com.tradehero.th.network.service;

import com.tradehero.th.api.auth.AccessTokenForm;
import com.tradehero.th.api.form.UserFormDTO;
import com.tradehero.th.api.social.SocialNetworkFormDTO;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.auth.AuthData;
import com.tradehero.th.models.user.DTOProcessorUpdateUserProfile;
import com.tradehero.th.network.retrofit.BaseMiddleCallback;
import com.tradehero.th.network.retrofit.MiddleCallback;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import retrofit.Callback;
import rx.Observable;
import rx.functions.Func1;

@Singleton public class SocialServiceWrapper
    implements SocialLinker
{
    @NotNull private final SocialService socialService;
    @NotNull private final SocialServiceAsync socialServiceAsync;
    @NotNull private final CurrentUserId currentUserId;
    @NotNull private final Provider<DTOProcessorUpdateUserProfile> dtoProcessorUpdateUserProfileProvider;

    @Inject public SocialServiceWrapper(
            @NotNull SocialService socialService,
            @NotNull SocialServiceAsync socialServiceAsync,
            @NotNull CurrentUserId currentUserId,
            @NotNull Provider<DTOProcessorUpdateUserProfile> dtoProcessorUpdateUserProfileProvider)
    {
        this.socialService = socialService;
        this.socialServiceAsync = socialServiceAsync;
        this.currentUserId = currentUserId;
        this.dtoProcessorUpdateUserProfileProvider = dtoProcessorUpdateUserProfileProvider;
    }

    //<editor-fold desc="Connect">
    @NotNull public UserProfileDTO connect(@NotNull UserBaseKey userBaseKey, UserFormDTO userFormDTO)
    {
        return dtoProcessorUpdateUserProfileProvider.get().process(socialService.connect(userBaseKey.key, userFormDTO));
    }

    @NotNull public UserProfileDTO connect(@NotNull UserBaseKey userBaseKey, AccessTokenForm userFormDTO)
    {
        return dtoProcessorUpdateUserProfileProvider.get().process(socialService.connect(userBaseKey.key, userFormDTO));
    }

    @NotNull public Func1<AuthData, UserProfileDTO> connectFunc1(@NotNull final UserBaseKey userBaseKey)
    {
        return new Func1<AuthData, UserProfileDTO>()
        {
            @Override public UserProfileDTO call(AuthData accessTokenForm)
            {
                return connect(userBaseKey, new AccessTokenForm(accessTokenForm));
            }
        };
    }

    @NotNull public Observable<UserProfileDTO> connectRx(@NotNull UserBaseKey userBaseKey, AccessTokenForm accessTokenForm)
    {
        return socialService.connectRx(userBaseKey.key, accessTokenForm)
                .map(new Func1<UserProfileDTO, UserProfileDTO>()
                {
                    @Override public UserProfileDTO call(UserProfileDTO userProfileDTO)
                    {
                        return dtoProcessorUpdateUserProfileProvider.get().process(userProfileDTO);
                    }
                });
    }

    @Override @NotNull public Observable<UserProfileDTO> link(AccessTokenForm accessTokenForm)
    {
        return connectRx(currentUserId.toUserBaseKey(), accessTokenForm);
    }

    @NotNull public MiddleCallback<UserProfileDTO> connect(@NotNull UserBaseKey userBaseKey, UserFormDTO userFormDTO, Callback<UserProfileDTO> callback)
    {
        MiddleCallback<UserProfileDTO> middleCallback = new BaseMiddleCallback<>(callback, dtoProcessorUpdateUserProfileProvider.get());
        socialServiceAsync.connect(userBaseKey.key, userFormDTO, middleCallback);
        return middleCallback;
    }
    //</editor-fold>

    //<editor-fold desc="Disconnect">
    public UserProfileDTO disconnect(@NotNull UserBaseKey userBaseKey, SocialNetworkFormDTO socialNetworkFormDTO)
    {
        return dtoProcessorUpdateUserProfileProvider.get().process(socialService.disconnect(userBaseKey.key, socialNetworkFormDTO));
    }

    public Observable<UserProfileDTO> disconnectRx(@NotNull UserBaseKey userBaseKey, SocialNetworkFormDTO socialNetworkFormDTO)
    {
        return socialService.disconnectRx(userBaseKey.key, socialNetworkFormDTO)
                .doOnNext(dtoProcessorUpdateUserProfileProvider.get());
    }

    public MiddleCallback<UserProfileDTO> disconnect(@NotNull UserBaseKey userBaseKey, SocialNetworkFormDTO socialNetworkFormDTO, Callback<UserProfileDTO> callback)
    {
        MiddleCallback<UserProfileDTO> middleCallback = new BaseMiddleCallback<>(callback, dtoProcessorUpdateUserProfileProvider.get());
        socialServiceAsync.disconnect(userBaseKey.key, socialNetworkFormDTO, middleCallback);
        return middleCallback;
    }
    //</editor-fold>
}
