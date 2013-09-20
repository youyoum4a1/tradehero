package com.tradehero.th.utills;

import android.util.Log;

@Deprecated public class Logger
{
    public enum LogLevel
    {
        LOGGING_LEVEL_DEBUG, LOGGING_LEVEL_ERROR, LOGGING_LEVEL_INFO, LOGGING_LEVEL_VERBOSE,
        LOGGING_LEVEL_WARN
    }

    public static final boolean ENABLE_LOGGING = true;

    private static final LogLevel CURRENT_LOGGING_LEVEL = LogLevel.LOGGING_LEVEL_VERBOSE;

    public static final void log(String tag, String message, LogLevel logLevel)
    {
        if (ENABLE_LOGGING
                && CURRENT_LOGGING_LEVEL.ordinal() >= logLevel.ordinal())
        {
            switch (logLevel)
            {
                case LOGGING_LEVEL_VERBOSE:
                    Log.v(tag, message);
                    break;
                case LOGGING_LEVEL_INFO:
                    Log.i(tag, message);
                    break;
                case LOGGING_LEVEL_DEBUG:
                    Log.d(tag, message);
                    break;
                case LOGGING_LEVEL_WARN:
                    Log.w(tag, message);
                    break;
                case LOGGING_LEVEL_ERROR:
                    Log.e(tag, message);
                    break;
            }
        }
    }

    public static void log1(String tag, String message,
            LogLevel loggingLevelDebug)
    {
        switch (loggingLevelDebug)
        {
            case LOGGING_LEVEL_VERBOSE:
                Log.v(tag, message);
                break;
            case LOGGING_LEVEL_INFO:
                Log.i(tag, message);
                break;
            case LOGGING_LEVEL_DEBUG:
                Log.d(tag, message);
                break;
            case LOGGING_LEVEL_WARN:
                Log.w(tag, message);
                break;
            case LOGGING_LEVEL_ERROR:
                Log.e(tag, message);
                break;
        }
    }
}
