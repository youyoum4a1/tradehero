package com.tradehero.th.network.service;

import com.tradehero.th.api.auth.AccessTokenForm;
import com.tradehero.th.api.form.UserFormDTO;
import com.tradehero.th.api.social.SocialNetworkFormDTO;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.auth.AuthData;
import com.tradehero.th.models.user.DTOProcessorUpdateUserProfile;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import rx.Observable;
import rx.functions.Func1;

@Singleton public class SocialServiceWrapper
    implements SocialLinker
{
    @NotNull private final SocialService socialService;
    @NotNull private final SocialServiceRx socialServiceRx;
    @NotNull private final CurrentUserId currentUserId;
    @NotNull private final Provider<DTOProcessorUpdateUserProfile> dtoProcessorUpdateUserProfileProvider;

    @Inject public SocialServiceWrapper(
            @NotNull SocialService socialService,
            @NotNull SocialServiceRx socialServiceRx,
            @NotNull CurrentUserId currentUserId,
            @NotNull Provider<DTOProcessorUpdateUserProfile> dtoProcessorUpdateUserProfileProvider)
    {
        this.socialService = socialService;
        this.socialServiceRx = socialServiceRx;
        this.currentUserId = currentUserId;
        this.dtoProcessorUpdateUserProfileProvider = dtoProcessorUpdateUserProfileProvider;
    }

    //<editor-fold desc="Connect">
    @NotNull public Observable<UserProfileDTO> connectRx(@NotNull UserBaseKey userBaseKey, UserFormDTO userFormDTO)
    {
        return socialServiceRx.connect(userBaseKey.key, userFormDTO)
                .doOnNext(dtoProcessorUpdateUserProfileProvider.get());
    }

    @NotNull public UserProfileDTO connect(@NotNull UserBaseKey userBaseKey, AccessTokenForm userFormDTO)
    {
        return dtoProcessorUpdateUserProfileProvider.get().process(socialService.connect(userBaseKey.key, userFormDTO));
    }

    @NotNull public Func1<AuthData, UserProfileDTO> connectFunc1(@NotNull final UserBaseKey userBaseKey)
    {
        return accessTokenForm -> connect(userBaseKey, new AccessTokenForm(accessTokenForm));
    }

    @NotNull public Observable<UserProfileDTO> connectRx(@NotNull UserBaseKey userBaseKey, AccessTokenForm accessTokenForm)
    {
        return socialServiceRx.connect(userBaseKey.key, accessTokenForm)
                .map(userProfileDTO -> dtoProcessorUpdateUserProfileProvider.get().process(userProfileDTO));
    }

    @Override @NotNull public Observable<UserProfileDTO> link(AccessTokenForm accessTokenForm)
    {
        return connectRx(currentUserId.toUserBaseKey(), accessTokenForm);
    }
    //</editor-fold>

    //<editor-fold desc="Disconnect">
    public Observable<UserProfileDTO> disconnectRx(@NotNull UserBaseKey userBaseKey, SocialNetworkFormDTO socialNetworkFormDTO)
    {
        return socialServiceRx.disconnect(userBaseKey.key, socialNetworkFormDTO)
                .doOnNext(dtoProcessorUpdateUserProfileProvider.get());
    }
    //</editor-fold>
}
