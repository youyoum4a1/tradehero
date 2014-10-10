package com.tradehero.th.network.service;

import com.tradehero.th.api.form.UserFormDTO;
import com.tradehero.th.api.social.SocialNetworkFormDTO;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.api.auth.AccessTokenForm;
import com.tradehero.th.auth.AuthData;
import com.tradehero.th.models.user.DTOProcessorUpdateUserProfile;
import com.tradehero.th.network.retrofit.BaseMiddleCallback;
import com.tradehero.th.network.retrofit.MiddleCallback;
import com.tradehero.th.persistence.home.HomeContentCache;
import com.tradehero.th.persistence.user.UserProfileCache;
import javax.inject.Inject;
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
    @NotNull private final UserProfileCache userProfileCache;
    @NotNull private final CurrentUserId currentUserId;
    @NotNull private final HomeContentCache homeContentCache;

    @Inject public SocialServiceWrapper(
            @NotNull SocialService socialService,
            @NotNull SocialServiceAsync socialServiceAsync,
            @NotNull UserProfileCache userProfileCache,
            @NotNull CurrentUserId currentUserId,
            @NotNull HomeContentCache homeContentCache)
    {
        this.socialService = socialService;
        this.socialServiceAsync = socialServiceAsync;
        this.userProfileCache = userProfileCache;
        this.currentUserId = currentUserId;
        this.homeContentCache = homeContentCache;
    }

    @NotNull protected DTOProcessorUpdateUserProfile createConnectDTOProcessor()
    {
        return new DTOProcessorUpdateUserProfile(userProfileCache, homeContentCache);
    }

    //<editor-fold desc="Connect">
    @NotNull public UserProfileDTO connect(@NotNull UserBaseKey userBaseKey, UserFormDTO userFormDTO)
    {
        return createConnectDTOProcessor().process(socialService.connect(userBaseKey.key, userFormDTO));
    }

    @NotNull public UserProfileDTO connect(@NotNull UserBaseKey userBaseKey, AccessTokenForm userFormDTO)
    {
        return createConnectDTOProcessor().process(socialService.connect(userBaseKey.key, userFormDTO));
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
                        return createConnectDTOProcessor().process(userProfileDTO);
                    }
                });
    }

    @Override @NotNull public Observable<UserProfileDTO> link(AccessTokenForm accessTokenForm)
    {
        return connectRx(currentUserId.toUserBaseKey(), accessTokenForm);
    }

    @NotNull public MiddleCallback<UserProfileDTO> connect(@NotNull UserBaseKey userBaseKey, UserFormDTO userFormDTO, Callback<UserProfileDTO> callback)
    {
        MiddleCallback<UserProfileDTO> middleCallback = new BaseMiddleCallback<>(callback, createConnectDTOProcessor());
        socialServiceAsync.connect(userBaseKey.key, userFormDTO, middleCallback);
        return middleCallback;
    }
    //</editor-fold>

    //<editor-fold desc="Disconnect">
    public UserProfileDTO disconnect(@NotNull UserBaseKey userBaseKey, SocialNetworkFormDTO socialNetworkFormDTO)
    {
        return createConnectDTOProcessor().process(socialService.disconnect(userBaseKey.key, socialNetworkFormDTO));
    }

    public Observable<UserProfileDTO> disconnectRx(@NotNull UserBaseKey userBaseKey, SocialNetworkFormDTO socialNetworkFormDTO)
    {
        return socialService.disconnectRx(userBaseKey.key, socialNetworkFormDTO)
                .doOnNext(createConnectDTOProcessor());
    }

    public MiddleCallback<UserProfileDTO> disconnect(@NotNull UserBaseKey userBaseKey, SocialNetworkFormDTO socialNetworkFormDTO, Callback<UserProfileDTO> callback)
    {
        MiddleCallback<UserProfileDTO> middleCallback = new BaseMiddleCallback<>(callback, createConnectDTOProcessor());
        socialServiceAsync.disconnect(userBaseKey.key, socialNetworkFormDTO, middleCallback);
        return middleCallback;
    }
    //</editor-fold>
}
