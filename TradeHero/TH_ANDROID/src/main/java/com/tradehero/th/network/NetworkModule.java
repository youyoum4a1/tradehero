package com.tradehero.th.network;

import android.content.SharedPreferences;
import com.tradehero.common.annotation.ForApp;
import com.tradehero.common.persistence.prefs.StringPreference;
import com.tradehero.th.api.users.LoginFormDTO;
import com.tradehero.th.api.users.signup.LoginSignUpFormDeviceDTO;
import com.tradehero.th.api.users.signup.LoginSignUpFormEmailDTO;
import com.tradehero.th.api.users.signup.LoginSignUpFormFacebookDTO;
import com.tradehero.th.api.users.signup.LoginSignUpFormLinkedinDTO;
import com.tradehero.th.api.users.signup.LoginSignUpFormQQDTO;
import com.tradehero.th.api.users.signup.LoginSignUpFormTwitterDTO;
import com.tradehero.th.api.users.signup.LoginSignUpFormWeChatDTO;
import com.tradehero.th.api.users.signup.LoginSignUpFormWeiboDTO;
import com.tradehero.th.base.Application;
import com.tradehero.th.models.push.DeviceTokenHelper;
import com.tradehero.th.network.retrofit.RetrofitModule;
import com.tradehero.th.utils.VersionUtils;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

@Module(
        includes = {
                RetrofitModule.class
        },
        complete = false,
        library = true
)
public class NetworkModule
{
    public static final String SERVER_ENDPOINT_KEY = "SERVER_ENDPOINT_KEY";

    @Provides @Singleton @ServerEndpoint
    StringPreference provideEndpointPreference(@ForApp SharedPreferences sharedPreferences)
    {
        return new StringPreference(sharedPreferences, SERVER_ENDPOINT_KEY, NetworkConstants.getApiEndPointInUse());
    }

    @Provides LoginFormDTO provideLoginFormDTO(DeviceTokenHelper deviceTokenHelper)
    {
        return new LoginFormDTO(
                deviceTokenHelper.getDeviceToken()/**PushManager.shared().getAPID()*/,
                deviceTokenHelper.getDeviceType() /**DeviceType.Android*/,
                VersionUtils.getVersionId(Application.context()),
                deviceTokenHelper.getIMEI());
    }

    @Provides LoginSignUpFormEmailDTO provideLoginSignUpFormEmailDTO(DeviceTokenHelper deviceTokenHelper)
    {
        return new LoginSignUpFormEmailDTO(
                deviceTokenHelper.getDeviceToken()/**PushManager.shared().getAPID()*/,
                deviceTokenHelper.getDeviceType() /**DeviceType.Android*/,
                VersionUtils.getVersionId(Application.context()),
                deviceTokenHelper.getIMEI());
    }

    @Provides LoginSignUpFormFacebookDTO provideLoginSignUpFormFacebookDTO(DeviceTokenHelper deviceTokenHelper)
    {
        return new LoginSignUpFormFacebookDTO(
                deviceTokenHelper.getDeviceToken()/**PushManager.shared().getAPID()*/,
                deviceTokenHelper.getDeviceType() /**DeviceType.Android*/,
                VersionUtils.getVersionId(Application.context()),
                deviceTokenHelper.getIMEI());
    }

    @Provides LoginSignUpFormLinkedinDTO provideLoginSignUpFormLinkedinDTO(DeviceTokenHelper deviceTokenHelper)
    {
        return new LoginSignUpFormLinkedinDTO(
                deviceTokenHelper.getDeviceToken()/**PushManager.shared().getAPID()*/,
                deviceTokenHelper.getDeviceType() /**DeviceType.Android*/,
                VersionUtils.getVersionId(Application.context()),
                deviceTokenHelper.getIMEI());
    }

    @Provides LoginSignUpFormQQDTO provideLoginSignUpFormQQDTO(DeviceTokenHelper deviceTokenHelper)
    {
        return new LoginSignUpFormQQDTO(
                deviceTokenHelper.getDeviceToken()/**PushManager.shared().getAPID()*/,
                deviceTokenHelper.getDeviceType() /**DeviceType.Android*/,
                VersionUtils.getVersionId(Application.context()),
                deviceTokenHelper.getIMEI());
    }

    @Provides LoginSignUpFormWeChatDTO provideLoginSignUpFormWechatDTO(DeviceTokenHelper deviceTokenHelper)
    {
        return new LoginSignUpFormWeChatDTO(
                deviceTokenHelper.getDeviceToken()/**PushManager.shared().getAPID()*/,
                deviceTokenHelper.getDeviceType() /**DeviceType.Android*/,
                VersionUtils.getVersionId(Application.context()),
                deviceTokenHelper.getIMEI());
    }

    @Provides LoginSignUpFormTwitterDTO provideLoginSignUpFormTwitterDTO(DeviceTokenHelper deviceTokenHelper)
    {
        return new LoginSignUpFormTwitterDTO(
                deviceTokenHelper.getDeviceToken()/**PushManager.shared().getAPID()*/,
                deviceTokenHelper.getDeviceType() /**DeviceType.Android*/,
                VersionUtils.getVersionId(Application.context()),
                deviceTokenHelper.getIMEI());
    }

    @Provides LoginSignUpFormWeiboDTO provideLoginSignUpFormWeiboDTO(DeviceTokenHelper deviceTokenHelper)
    {
        return new LoginSignUpFormWeiboDTO(
                deviceTokenHelper.getDeviceToken()/**PushManager.shared().getAPID()*/,
                deviceTokenHelper.getDeviceType() /**DeviceType.Android*/,
                VersionUtils.getVersionId(Application.context()),
                deviceTokenHelper.getIMEI());
    }

    @Provides LoginSignUpFormDeviceDTO provideLoginSignUpFormDeviceDTO(DeviceTokenHelper deviceTokenHelper)
    {
        return new LoginSignUpFormDeviceDTO(
                deviceTokenHelper.getDeviceToken()/**PushManager.shared().getAPID()*/,
                deviceTokenHelper.getDeviceType() /**DeviceType.Android*/,
                VersionUtils.getVersionId(Application.context()),
                deviceTokenHelper.getIMEI());
    }
}
