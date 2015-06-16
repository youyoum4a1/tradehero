package com.tradehero.th.models.push.urbanairship;

import android.content.Context;
import com.tradehero.th.utils.Constants;
import com.urbanairship.AirshipConfigOptions;
import dagger.Module;
import dagger.Provides;

@Module(
        injects = {
        },
        complete = false,
        library = true
)
public class UrbanAirshipPushModule
{
    @Provides AirshipConfigOptions provideAirshipConfigOptions(Context context)
    {
        AirshipConfigOptions options = AirshipConfigOptions.loadDefaultOptions(context);
        if (Constants.DOGFOOD_BUILD)
        {
            options.inProduction = false;
            options.gcmSender = Constants.GCM_STAGING_SENDER;
        }
        return options;
    }
}
