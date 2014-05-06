package com.tradehero.common.log;

import com.crashlytics.android.Crashlytics;
import timber.log.Timber;


public class CrashReportingTree extends Timber.HollowTree
{
    @Override public void e(Throwable t, String message, Object... args)
    {
        Crashlytics.logException(t);
    }
}
