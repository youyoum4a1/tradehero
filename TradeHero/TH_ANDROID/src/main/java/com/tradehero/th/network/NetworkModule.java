package com.tradehero.th.network;

import android.content.SharedPreferences;
import com.tradehero.common.annotation.ForApp;
import com.tradehero.common.persistence.prefs.StringPreference;
import com.tradehero.th.api.users.LoginFormDTO;
import com.tradehero.th.api.users.signup.LoginSignUpFormEmailDTO;
import com.tradehero.th.api.users.signup.LoginSignUpFormFacebookDTO;
import com.tradehero.th.api.users.signup.LoginSignUpFormLinkedinDTO;
import com.tradehero.th.api.users.signup.LoginSignUpFormQQDTO;
import com.tradehero.th.api.users.signup.LoginSignUpFormTwitterDTO;
import com.tradehero.th.api.users.signup.LoginSignUpFormWeiboDTO;
import com.tradehero.th.base.THApp;
import com.tradehero.th.network.retrofit.RetrofitModule;
import com.tradehero.th.persistence.prefs.SavedPushDeviceIdentifier;
import com.tradehero.th.utils.Constants;
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

    // TODO FIXME all below methods look the same to me, wtf??? Possible to use constructor injection here ...

    @Provides LoginFormDTO provideLoginFormDTO(@SavedPushDeviceIdentifier StringPreference savedPushIdentifier)
    {
        return new LoginFormDTO(
                savedPushIdentifier.get(),
                Constants.DEVICE_TYPE /**DeviceType.Android*/,
                VersionUtils.getVersionId(THApp.context()));
    }

    @Provides LoginSignUpFormEmailDTO provideLoginSignUpFormEmailDTO(@SavedPushDeviceIdentifier StringPreference savedPushIdentifier)
    {
        return new LoginSignUpFormEmailDTO(
                savedPushIdentifier.get(),
                Constants.DEVICE_TYPE /**DeviceType.Android*/,
                VersionUtils.getVersionId(THApp.context()));
    }

    @Provides LoginSignUpFormFacebookDTO provideLoginSignUpFormFacebookDTO(@SavedPushDeviceIdentifier StringPreference savedPushIdentifier)
    {
        return new LoginSignUpFormFacebookDTO(
                savedPushIdentifier.get(),
                Constants.DEVICE_TYPE /**DeviceType.Android*/,
                VersionUtils.getVersionId(THApp.context()));
    }

    @Provides LoginSignUpFormLinkedinDTO provideLoginSignUpFormLinkedinDTO(@SavedPushDeviceIdentifier StringPreference savedPushIdentifier)
    {
        return new LoginSignUpFormLinkedinDTO(
                savedPushIdentifier.get(),
                Constants.DEVICE_TYPE /**DeviceType.Android*/,
                VersionUtils.getVersionId(THApp.context()));
    }

    @Provides LoginSignUpFormQQDTO provideLoginSignUpFormQQDTO(@SavedPushDeviceIdentifier StringPreference savedPushIdentifier)
    {
        return new LoginSignUpFormQQDTO(
                savedPushIdentifier.get(),
                Constants.DEVICE_TYPE /**DeviceType.Android*/,
                VersionUtils.getVersionId(THApp.context()));
    }

    @Provides LoginSignUpFormTwitterDTO provideLoginSignUpFormTwitterDTO(@SavedPushDeviceIdentifier StringPreference savedPushIdentifier)
    {
        return new LoginSignUpFormTwitterDTO(
                savedPushIdentifier.get(),
                Constants.DEVICE_TYPE /**DeviceType.Android*/,
                VersionUtils.getVersionId(THApp.context()));
    }

    @Provides LoginSignUpFormWeiboDTO provideLoginSignUpFormWeiboDTO(@SavedPushDeviceIdentifier StringPreference savedPushIdentifier)
    {
        return new LoginSignUpFormWeiboDTO(
                savedPushIdentifier.get(),
                Constants.DEVICE_TYPE /**DeviceType.Android*/,
                VersionUtils.getVersionId(THApp.context()));
    }
}
