package com.tradehero.th.network.service;

import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.models.user.MiddleCallbackLogout;
import com.tradehero.th.network.retrofit.MiddleCallback;
import javax.inject.Inject;
import javax.inject.Singleton;
import retrofit.Callback;

/**
 * Created by xavier on 3/5/14.
 */
@Singleton public class SessionServiceWrapper
{
    public static final String TAG = SessionServiceWrapper.class.getSimpleName();

    @Inject SessionService sessionService;

    @Inject public SessionServiceWrapper()
    {
        super();
    }

    public MiddleCallback<UserProfileDTO> logout(Callback<UserProfileDTO> callback)
    {
        MiddleCallbackLogout middleCallback = new MiddleCallbackLogout(callback);
        sessionService.logout(middleCallback);
        return middleCallback;
    }
}
