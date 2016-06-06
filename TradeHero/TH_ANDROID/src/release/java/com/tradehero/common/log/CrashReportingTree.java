package com.androidth.general.common.log;

import android.util.Log;
import com.crashlytics.android.Crashlytics;
import java.util.HashMap;
import java.util.Map;
import timber.log.Timber;

public class CrashReportingTree extends Timber.Tree
{
    private final Map<Integer, String> priorities;

    public CrashReportingTree()
    {
        this.priorities = new HashMap<>();
        priorities.put(Log.VERBOSE, "Verbose");
        priorities.put(Log.DEBUG, "Debug");
        priorities.put(Log.INFO, "Info");
        priorities.put(Log.WARN, "Warn");
        priorities.put(Log.ERROR, "Error");
        priorities.put(Log.ASSERT, "Assert");
    }

    @Override protected void log(int priority, String tag, String message, Throwable cause)
    {
        if (cause == null)
        {
            Crashlytics.log(priority, tag, message);
        }
        else
        {
            String priorityString = priorities.get(priority);
            if (priorityString == null)
            {
                priorityString = "" + priority;
            }
            Crashlytics.logException(new CrashlyticsReportException(priorityString, tag, message, cause));
        };
    }
}
