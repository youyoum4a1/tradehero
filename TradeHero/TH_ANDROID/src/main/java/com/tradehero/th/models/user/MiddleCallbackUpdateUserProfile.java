package com.tradehero.th.models.user;

import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.network.retrofit.MiddleCallback;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.utils.DaggerUtils;
import javax.inject.Inject;
import retrofit.Callback;
import retrofit.client.Response;

/**
 * Created by xavier on 3/6/14.
 */
public class MiddleCallbackUpdateUserProfile extends MiddleCallback<UserProfileDTO>
{
    public static final String TAG = MiddleCallbackUpdateUserProfile.class.getSimpleName();

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
