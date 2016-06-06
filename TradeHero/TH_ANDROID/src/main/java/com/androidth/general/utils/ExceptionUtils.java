package com.androidth.general.utils;

import android.support.annotation.NonNull;
import java.util.ArrayList;
import java.util.List;

public class ExceptionUtils
{
    @NonNull public static List<String> getElements(@NonNull Throwable exception)
    {
        List<String> reported = new ArrayList<>();

        reported.add(exception.getClass().getName());
        reported.add(exception.getMessage());
        reported.addAll(getStacktrace(exception));

        return reported;
    }

    @NonNull public static List<String> getStacktrace(@NonNull Throwable exception)
    {
        List<String> reported = new ArrayList<>();

        for (StackTraceElement stackTraceElement : exception.getStackTrace())
        {
            reported.add(stackTraceElement.toString());
        }

        return reported;
    }
}
