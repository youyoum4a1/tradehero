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
import android.support.annotation.NonNull;
import rx.Observable;
import rx.functions.Func1;

@Singleton public class SocialServiceWrapper
    implements SocialLinker
{
    @NonNull private final SocialService socialService;
    @NonNull private final SocialServiceRx socialServiceRx;
    @NonNull private final CurrentUserId currentUserId;
    @NonNull private final Provider<DTOProcessorUpdateUserProfile> dtoProcessorUpdateUserProfileProvider;

    @Inject public SocialServiceWrapper(
            @NonNull SocialService socialService,
            @NonNull SocialServiceRx socialServiceRx,
            @NonNull CurrentUserId currentUserId,
            @NonNull Provider<DTOProcessorUpdateUserProfile> dtoProcessorUpdateUserProfileProvider)
    {
        this.socialService = socialService;
        this.socialServiceRx = socialServiceRx;
        this.currentUserId = currentUserId;
        this.dtoProcessorUpdateUserProfileProvider = dtoProcessorUpdateUserProfileProvider;
    }

    //<editor-fold desc="Connect">
    @NonNull public Observable<UserProfileDTO> connectRx(@NonNull UserBaseKey userBaseKey, UserFormDTO userFormDTO)
    {
        return socialServiceRx.connect(userBaseKey.key, userFormDTO)
                .doOnNext(dtoProcessorUpdateUserProfileProvider.get());
    }

    @NonNull public UserProfileDTO connect(@NonNull UserBaseKey userBaseKey, AccessTokenForm userFormDTO)
    {
        return dtoProcessorUpdateUserProfileProvider.get().process(socialService.connect(userBaseKey.key, userFormDTO));
    }

    @NonNull public Func1<AuthData, UserProfileDTO> connectFunc1(@NonNull final UserBaseKey userBaseKey)
    {
        return accessTokenForm -> connect(userBaseKey, new AccessTokenForm(accessTokenForm));
    }

    @NonNull public Observable<UserProfileDTO> connectRx(@NonNull UserBaseKey userBaseKey, AccessTokenForm accessTokenForm)
    {
        return socialServiceRx.connect(userBaseKey.key, accessTokenForm)
                .map(userProfileDTO -> dtoProcessorUpdateUserProfileProvider.get().process(userProfileDTO));
    }

    @Override @NonNull public Observable<UserProfileDTO> link(AccessTokenForm accessTokenForm)
    {
        return connectRx(currentUserId.toUserBaseKey(), accessTokenForm);
    }
    //</editor-fold>

    //<editor-fold desc="Disconnect">
    public Observable<UserProfileDTO> disconnectRx(@NonNull UserBaseKey userBaseKey, SocialNetworkFormDTO socialNetworkFormDTO)
    {
        return socialServiceRx.disconnect(userBaseKey.key, socialNetworkFormDTO)
                .doOnNext(dtoProcessorUpdateUserProfileProvider.get());
    }
    //</editor-fold>
}
