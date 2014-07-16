package com.tradehero.th.network;

import android.content.SharedPreferences;
import com.tradehero.common.annotation.ForApp;
import com.tradehero.common.persistence.prefs.StringPreference;
import com.tradehero.th.api.users.LoginFormDTO;
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
        return new StringPreference(sharedPreferences, SERVER_ENDPOINT_KEY, NetworkConstants.TRADEHERO_PROD_API_ENDPOINT);
    }

    @Provides
    LoginFormDTO provideLoginFormDTO()
    {
        return new LoginFormDTO(
                DeviceTokenHelper.getDeviceToken()/**PushManager.shared().getAPID()*/,
                DeviceTokenHelper.getDeviceType() /**DeviceType.Android*/,
                VersionUtils.getVersionId(Application.context()));
    }
}
