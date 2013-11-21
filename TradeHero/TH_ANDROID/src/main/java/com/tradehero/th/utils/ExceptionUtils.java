package com.tradehero.th.utils;

import java.util.ArrayList;
import java.util.List;

/** Created with IntelliJ IDEA. User: xavier Date: 11/21/13 Time: 12:05 PM To change this template use File | Settings | File Templates. */
public class ExceptionUtils
{
    public static final String TAG = ExceptionUtils.class.getSimpleName();

    public static List<String> getElements(Exception exception)
    {
        List<String> reported = new ArrayList<>();

        if (exception != null)
        {
            reported.add(exception.getClass().getName());
            reported.add(exception.getMessage());
            reported.addAll(getStacktrace(exception));
        }

        return reported;
    }

    public static List<String> getStacktrace(Exception exception)
    {
        List<String> reported = new ArrayList<>();

        if (exception != null)
        {
            for (StackTraceElement stackTraceElement : exception.getStackTrace())
            {
                reported.add(stackTraceElement.toString());
            }
        }

        return reported;
    }
}
