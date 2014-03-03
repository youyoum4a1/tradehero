package com.tradehero.common.log;

import com.crashlytics.android.Crashlytics;
import timber.log.Timber;

/**
 * Created with IntelliJ IDEA. User: tho Date: 2/20/14 Time: 2:58 PM Copyright (c) TradeHero
 */
public class CrashReportingTree extends Timber.HollowTree
{
    @Override public void e(Throwable t, String message, Object... args)
    {
        Crashlytics.logException(t);
    }
}
