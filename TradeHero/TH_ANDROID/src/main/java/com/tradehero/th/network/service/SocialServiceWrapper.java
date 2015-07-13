package com.tradehero.th.network.service;

import com.tradehero.th.api.form.UserFormDTO;
import com.tradehero.th.api.social.SocialNetworkFormDTO;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.models.DTOProcessor;
import com.tradehero.th.models.user.DTOProcessorUpdateUserProfile;
import com.tradehero.th.network.retrofit.BaseMiddleCallback;
import com.tradehero.th.network.retrofit.MiddleCallback;
import com.tradehero.th.persistence.user.UserProfileCache;
import org.jetbrains.annotations.NotNull;
import retrofit.Callback;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton public class SocialServiceWrapper
{
    @NotNull private final SocialServiceAsync socialServiceAsync;
    @NotNull private final UserProfileCache userProfileCache;

    @Inject public SocialServiceWrapper(
            @NotNull SocialServiceAsync socialServiceAsync,
            @NotNull UserProfileCache userProfileCache)
    {
        this.socialServiceAsync = socialServiceAsync;
        this.userProfileCache = userProfileCache;
    }

    protected DTOProcessor<UserProfileDTO> createConnectDTOProcessor()
    {
        return new DTOProcessorUpdateUserProfile(userProfileCache);
    }

    public MiddleCallback<UserProfileDTO> connect(UserBaseKey userBaseKey, UserFormDTO userFormDTO, Callback<UserProfileDTO> callback)
    {
        MiddleCallback<UserProfileDTO> middleCallback = new BaseMiddleCallback<>(callback, createConnectDTOProcessor());
        socialServiceAsync.connect(userBaseKey.key, userFormDTO, middleCallback);
        return middleCallback;
    }
    //</editor-fold>

    public MiddleCallback<UserProfileDTO> disconnect(UserBaseKey userBaseKey, SocialNetworkFormDTO socialNetworkFormDTO, Callback<UserProfileDTO> callback)
    {
        MiddleCallback<UserProfileDTO> middleCallback = new BaseMiddleCallback<>(callback, createConnectDTOProcessor());
        socialServiceAsync.disconnect(userBaseKey.key, socialNetworkFormDTO, middleCallback);
        return middleCallback;
    }


    //</editor-fold>
}
