package com.tradehero.th.activities;

import android.support.annotation.NonNull;
import com.crashlytics.android.Crashlytics;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.utils.Constants;
import java.util.Date;

public class ActivityBuildTypeUtil
{
    public static void setUpCrashReports(@NonNull UserBaseKey currentUserKey)
    {
        Crashlytics.setString(Constants.TH_CLIENT_TYPE,
                String.format("%s:%d", Constants.DEVICE_TYPE, Constants.TAP_STREAM_TYPE.type));
        Crashlytics.setUserIdentifier("" + currentUserKey.key);
    }

    public static void flagLowMemory()
    {
        Crashlytics.setString("LowMemoryAt", new Date().toString());
    }
}
