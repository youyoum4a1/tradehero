package com.tradehero.common.log;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class CrashlyticsReportException extends Exception
{
    public CrashlyticsReportException(@NonNull String priority, @Nullable String tag, @NonNull String detailMessage, @NonNull Throwable throwable)
    {
        super(createMessage(priority, tag, detailMessage), throwable);
    }

    @NonNull private static String createMessage(@NonNull String priority, @Nullable String tag, @NonNull String detailMessage)
    {
        if (tag == null)
        {
            return String.format("p: %s. \nm: %s",
                    priority,
                    detailMessage);
        }
        return String.format("p: %s. \nt: %s. \nm: %s",
                priority,
                tag,
                detailMessage);
    }
}
