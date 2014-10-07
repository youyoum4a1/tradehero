package com.tradehero.th.network.service;

import com.tradehero.th.api.form.UserFormDTO;
import com.tradehero.th.api.social.SocialNetworkFormDTO;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.models.DTOProcessor;
import com.tradehero.th.models.user.DTOProcessorUpdateUserProfile;
import com.tradehero.th.network.retrofit.BaseMiddleCallback;
import com.tradehero.th.network.retrofit.MiddleCallback;
import com.tradehero.th.persistence.home.HomeContentCache;
import com.tradehero.th.persistence.user.UserProfileCache;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import retrofit.Callback;

@Singleton public class SocialServiceWrapper
{
    @NotNull private final SocialService socialService;
    @NotNull private final SocialServiceAsync socialServiceAsync;
    @NotNull private final UserProfileCache userProfileCache;
    @NotNull private final HomeContentCache homeContentCache;

    @Inject public SocialServiceWrapper(
            @NotNull SocialService socialService,
            @NotNull SocialServiceAsync socialServiceAsync,
            @NotNull UserProfileCache userProfileCache,
            @NotNull HomeContentCache homeContentCache)
    {
        this.socialService = socialService;
        this.socialServiceAsync = socialServiceAsync;
        this.userProfileCache = userProfileCache;
        this.homeContentCache = homeContentCache;
    }

    protected DTOProcessor<UserProfileDTO> createConnectDTOProcessor()
    {
        return new DTOProcessorUpdateUserProfile(userProfileCache, homeContentCache);
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
