package com.tradehero.th.base;

import android.app.Application;
import android.support.annotation.NonNull;
import com.crashlytics.android.Crashlytics;
import com.tradehero.common.log.CrashReportingTree;
import com.tradehero.th.BuildConfig;
import timber.log.Timber;

public class TimberUtil
{
    public static Timber.Tree createTree(@NonNull Application application)
    {
        //noinspection PointlessBooleanExpression,ConstantConditions
        if (!BuildConfig.IS_INTELLIJ)
        {
            Crashlytics.start(application);
        }
        return new CrashReportingTree();
    }
}
