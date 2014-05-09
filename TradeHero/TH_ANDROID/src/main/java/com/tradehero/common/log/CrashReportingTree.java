package com.tradehero.common.log;

import com.crashlytics.android.Crashlytics;
import timber.log.Timber;


public class CrashReportingTree extends Timber.HollowTree
{
    @Override public void e(Throwable cause, String message, Object... args)
    {
        Crashlytics.logException(new Exception(
                    getConcatMessage(cause, message, args),
                    cause));
    }

    public String getConcatMessage(Throwable cause, String message, Object... args)
    {
        return String.format(
                "Message: %s\nCause: %s",
                String.format(message, args),
                cause.getMessage());
    }
}
