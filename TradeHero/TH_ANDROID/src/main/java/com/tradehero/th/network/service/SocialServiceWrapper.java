package com.ayondo.academy.network.service;

import android.support.annotation.NonNull;
import com.ayondo.academy.api.BaseResponseDTO;
import com.ayondo.academy.api.auth.AccessTokenForm;
import com.ayondo.academy.api.form.UserFormDTO;
import com.ayondo.academy.api.social.ReferralCodeShareFormDTO;
import com.ayondo.academy.api.social.SocialNetworkFormDTO;
import com.ayondo.academy.api.users.CurrentUserId;
import com.ayondo.academy.api.users.UserBaseKey;
import com.ayondo.academy.api.users.UserProfileDTO;
import com.ayondo.academy.auth.AuthData;
import com.ayondo.academy.models.user.DTOProcessorUpdateUserProfile;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import rx.Observable;
import rx.functions.Func1;

@Singleton public class SocialServiceWrapper
    implements SocialLinker
{
    @NonNull private final SocialServiceRx socialServiceRx;
    @NonNull private final CurrentUserId currentUserId;
    @NonNull private final Provider<DTOProcessorUpdateUserProfile> dtoProcessorUpdateUserProfileProvider;

    //<editor-fold desc="Constructors">
    @Inject public SocialServiceWrapper(
            @NonNull SocialServiceRx socialServiceRx,
            @NonNull CurrentUserId currentUserId,
            @NonNull Provider<DTOProcessorUpdateUserProfile> dtoProcessorUpdateUserProfileProvider)
    {
        this.socialServiceRx = socialServiceRx;
        this.currentUserId = currentUserId;
        this.dtoProcessorUpdateUserProfileProvider = dtoProcessorUpdateUserProfileProvider;
    }
    //</editor-fold>

    //<editor-fold desc="Connect">
    @NonNull public Observable<UserProfileDTO> connectRx(@NonNull UserBaseKey userBaseKey, UserFormDTO userFormDTO)
    {
        return socialServiceRx.connect(userBaseKey.key, userFormDTO)
                .map(dtoProcessorUpdateUserProfileProvider.get());
    }

    @NonNull public Func1<AuthData, Observable<UserProfileDTO>> connectFunc1(@NonNull final UserBaseKey userBaseKey)
    {
        return new Func1<AuthData, Observable<UserProfileDTO>>()
        {
            @Override public Observable<UserProfileDTO> call(AuthData accessTokenForm)
            {
                return SocialServiceWrapper.this.connectRx(userBaseKey, new AccessTokenForm(accessTokenForm));
            }
        };
    }

    @NonNull public Observable<UserProfileDTO> connectRx(@NonNull UserBaseKey userBaseKey, AccessTokenForm accessTokenForm)
    {
        return socialServiceRx.connect(userBaseKey.key, accessTokenForm)
                .map(dtoProcessorUpdateUserProfileProvider.get());
    }

    @Override @NonNull public Observable<UserProfileDTO> link(AccessTokenForm accessTokenForm)
    {
        return connectRx(currentUserId.toUserBaseKey(), accessTokenForm);
    }
    //</editor-fold>

    //<editor-fold desc="Disconnect">
    @NonNull public Observable<UserProfileDTO> disconnectRx(
            @NonNull UserBaseKey userBaseKey,
            @NonNull SocialNetworkFormDTO socialNetworkFormDTO)
    {
        return socialServiceRx.disconnect(userBaseKey.key, socialNetworkFormDTO)
                .map(dtoProcessorUpdateUserProfileProvider.get());
    }
    //</editor-fold>

    //<editor-fold desc="Share Referral Code">
    @NonNull public Observable<BaseResponseDTO> shareReferralCodeRx(
            @NonNull ReferralCodeShareFormDTO reqFormDTO)
    {
        return socialServiceRx.shareReferralCode(reqFormDTO);
    }
    //</editor-fold>
}
