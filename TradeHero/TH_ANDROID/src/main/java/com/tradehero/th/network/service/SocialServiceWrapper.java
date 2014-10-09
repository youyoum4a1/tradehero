package com.tradehero.th.network.service;

import com.tradehero.th.api.form.UserFormDTO;
import com.tradehero.th.api.social.SocialNetworkFormDTO;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.auth.AccessTokenForm;
import com.tradehero.th.auth.AuthData;
import com.tradehero.th.models.DTOProcessor;
import com.tradehero.th.models.user.DTOProcessorUpdateUserProfile;
import com.tradehero.th.network.retrofit.BaseMiddleCallback;
import com.tradehero.th.network.retrofit.MiddleCallback;
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

    @Inject public SocialServiceWrapper(
            @NotNull SocialService socialService,
            @NotNull SocialServiceAsync socialServiceAsync,
            @NotNull UserProfileCache userProfileCache,
            @NotNull CurrentUserId currentUserId)
    {
        this.socialService = socialService;
        this.socialServiceAsync = socialServiceAsync;
        this.userProfileCache = userProfileCache;
        this.currentUserId = currentUserId;
    }

    @NotNull protected DTOProcessor<UserProfileDTO> createConnectDTOProcessor()
    {
        return new DTOProcessorUpdateUserProfile(userProfileCache);
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

    @NotNull public Observable<UserProfileDTO> connectRx(@NotNull UserBaseKey userBaseKey, AccessTokenForm userFormDTO)
    {
        return socialService.connectRx(userBaseKey.key, userFormDTO)
                .map(new Func1<UserProfileDTO, UserProfileDTO>()
                {
                    @Override public UserProfileDTO call(UserProfileDTO userProfileDTO)
                    {
                        return createConnectDTOProcessor().process(userProfileDTO);
                    }
                });
    }

    @Override @NotNull public Observable<UserProfileDTO> link(AccessTokenForm userFormDTO)
    {
        return connectRx(currentUserId.toUserBaseKey(), userFormDTO);
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

    public MiddleCallback<UserProfileDTO> disconnect(@NotNull UserBaseKey userBaseKey, SocialNetworkFormDTO socialNetworkFormDTO, Callback<UserProfileDTO> callback)
    {
        MiddleCallback<UserProfileDTO> middleCallback = new BaseMiddleCallback<>(callback, createConnectDTOProcessor());
        socialServiceAsync.disconnect(userBaseKey.key, socialNetworkFormDTO, middleCallback);
        return middleCallback;
    }
    //</editor-fold>
}
