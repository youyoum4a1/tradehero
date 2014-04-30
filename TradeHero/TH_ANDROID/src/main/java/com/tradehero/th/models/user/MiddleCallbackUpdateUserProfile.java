package com.tradehero.th.models.user;

import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.network.retrofit.BaseMiddleCallback;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.utils.DaggerUtils;
import javax.inject.Inject;
import retrofit.Callback;
import retrofit.client.Response;

public class MiddleCallbackUpdateUserProfile extends BaseMiddleCallback<UserProfileDTO>
{
    @Inject UserProfileCache userProfileCache;

    public MiddleCallbackUpdateUserProfile(Callback<UserProfileDTO> primaryCallback)
    {
        super(primaryCallback);
        DaggerUtils.inject(this);
    }

    @Override public void success(UserProfileDTO userProfileDTO, Response response)
    {
        userProfileCache.put(userProfileDTO.getBaseKey(), userProfileDTO);
        super.success(userProfileDTO, response);
    }
}
