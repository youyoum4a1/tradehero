package com.tradehero.th.network.service;

import com.tradehero.th.api.form.UserFormDTO;
import com.tradehero.th.api.social.SocialNetworkFormDTO;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.models.user.MiddleCallbackUpdateUserProfile;
import javax.inject.Inject;
import javax.inject.Singleton;
import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.Path;

/**
 * Created by xavier on 3/7/14.
 */
@Singleton public class SocialServiceWrapper
{
    private final SocialService socialService;
    private final SocialServiceAsync socialServiceAsync;

    @Inject public SocialServiceWrapper(SocialService socialService, SocialServiceAsync socialServiceAsync)
    {
        this.socialService = socialService;
        this.socialServiceAsync = socialServiceAsync;
    }

    public MiddleCallbackUpdateUserProfile connect(UserBaseKey userBaseKey, UserFormDTO userFormDTO, Callback<UserProfileDTO> callback)
    {
        MiddleCallbackUpdateUserProfile middleCallbackConnect = new MiddleCallbackUpdateUserProfile(callback);
        socialServiceAsync.connect(userBaseKey.key, userFormDTO, middleCallbackConnect);
        return middleCallbackConnect;
    }

    public MiddleCallbackUpdateUserProfile disconnect(UserBaseKey userBaseKey, SocialNetworkFormDTO socialNetworkFormDTO, Callback<UserProfileDTO> callback)
    {
        MiddleCallbackUpdateUserProfile middleCallbackDisconnect = new MiddleCallbackUpdateUserProfile(callback);
        socialServiceAsync.disconnect(userBaseKey.key, socialNetworkFormDTO, middleCallbackDisconnect);
        return middleCallbackDisconnect;
    }
}
