package com.androidth.general.network.service;

import android.support.annotation.NonNull;
import com.androidth.general.api.BaseResponseDTO;
import com.androidth.general.api.auth.AccessTokenForm;
import com.androidth.general.api.form.UserFormDTO;
import com.androidth.general.api.social.ReferralCodeShareFormDTO;
import com.androidth.general.api.social.SocialNetworkFormDTO;
import com.androidth.general.api.users.CurrentUserId;
import com.androidth.general.api.users.UserBaseKey;
import com.androidth.general.api.users.UserProfileDTO;
import com.androidth.general.auth.AuthData;
import com.androidth.general.models.user.DTOProcessorUpdateUserProfile;
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