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
import javax.inject.Inject;
import javax.inject.Singleton;
import retrofit.Callback;

@Singleton public class SocialServiceWrapper
{
    private final SocialService socialService;
    private final SocialServiceAsync socialServiceAsync;
    private final UserProfileCache userProfileCache;

    @Inject public SocialServiceWrapper(
            SocialService socialService,
            SocialServiceAsync socialServiceAsync,
            UserProfileCache userProfileCache)
    {
        this.socialService = socialService;
        this.socialServiceAsync = socialServiceAsync;
        this.userProfileCache = userProfileCache;
    }

    protected DTOProcessor<UserProfileDTO> createConnectDTOProcessor()
    {
        return new DTOProcessorUpdateUserProfile(userProfileCache);
    }

    //<editor-fold desc="Connect">
    public UserProfileDTO connect(UserBaseKey userBaseKey, UserFormDTO userFormDTO)
    {
        return createConnectDTOProcessor().process(socialService.connect(userBaseKey.key, userFormDTO));
    }

    public MiddleCallback<UserProfileDTO> connect(UserBaseKey userBaseKey, UserFormDTO userFormDTO, Callback<UserProfileDTO> callback)
    {
        MiddleCallback<UserProfileDTO> middleCallback = new BaseMiddleCallback<>(callback, createConnectDTOProcessor());
        socialServiceAsync.connect(userBaseKey.key, userFormDTO, middleCallback);
        return middleCallback;
    }
    //</editor-fold>

    //<editor-fold desc="Disconnect">
    public UserProfileDTO disconnect(UserBaseKey userBaseKey, SocialNetworkFormDTO socialNetworkFormDTO)
    {
        return createConnectDTOProcessor().process(socialService.disconnect(userBaseKey.key, socialNetworkFormDTO));
    }

    public MiddleCallback<UserProfileDTO> disconnect(UserBaseKey userBaseKey, SocialNetworkFormDTO socialNetworkFormDTO, Callback<UserProfileDTO> callback)
    {
        MiddleCallback<UserProfileDTO> middleCallback = new BaseMiddleCallback<>(callback, createConnectDTOProcessor());
        socialServiceAsync.disconnect(userBaseKey.key, socialNetworkFormDTO, middleCallback);
        return middleCallback;
    }
    //</editor-fold>
}
