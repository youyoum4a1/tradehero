package com.tradehero.common.log;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import com.crashlytics.android.Crashlytics;
import timber.log.Timber;

public class CrashReportingTree extends Timber.HollowTree
{
    @Override public void e(@NonNull Throwable cause, @Nullable String message, Object... args)
    {
        if (message == null || TextUtils.isEmpty(message))
        {
            Crashlytics.logException(cause);
        }
        else
        {
            Crashlytics.logException(new Exception(
                    getConcatMessage(cause, message, args),
                    cause));
        }
    }

    @NonNull public String getConcatMessage(@NonNull Throwable cause, @NonNull String message, Object... args)
    {
        return String.format(
                "Message: %s%nCause: %s",
                String.format(message, args),
                cause.getMessage());
    }
}
