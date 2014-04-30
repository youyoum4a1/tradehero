package com.tradehero.th.network.service;

import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.models.push.baidu.BaiduDeviceMode;
import com.tradehero.th.models.user.MiddleCallbackLogout;
import com.tradehero.th.network.retrofit.BaseMiddleCallback;
import javax.inject.Inject;
import javax.inject.Singleton;
import retrofit.Callback;

/**
 * Created by xavier on 3/5/14.
 */
@Singleton public class SessionServiceWrapper
{
    private final SessionService sessionService;

    @Inject public SessionServiceWrapper(SessionService sessionService)
    {
        super();
        this.sessionService = sessionService;
    }

    public BaseMiddleCallback<UserProfileDTO> logout(Callback<UserProfileDTO> callback)
    {
        MiddleCallbackLogout middleCallback = new MiddleCallbackLogout(callback);
        sessionService.logout(middleCallback);
        return middleCallback;
    }

    public void updateDevice(BaiduDeviceMode deviceMode, Callback<UserProfileDTO> callback)
    {
        MiddleCallbackLogout middleCallback = new MiddleCallbackLogout(callback);
        sessionService.updateDevice(deviceMode.token,middleCallback);

    }



}
