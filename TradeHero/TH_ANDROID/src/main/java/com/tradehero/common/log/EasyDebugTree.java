package com.tradehero.common.log;

import android.util.Log;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import timber.log.Timber;

public class EasyDebugTree implements Timber.Tree, Timber.TaggedTree
{
    public static final Pattern ANONYMOUS_CLASS = Pattern.compile("\\$\\d+$");
    private static final ThreadLocal<String> NEXT_TAG = new ThreadLocal<>();

    protected String getTag()
    {
        String tag = NEXT_TAG.get();
        if (tag != null)
        {
            NEXT_TAG.remove();
            return tag;
        }

        return createTag();
    }

    public String createTag()
    {
        return getStackTraceClass();
    }

    public static String getStackTraceClass()
    {
        StackTraceElement element = new Throwable().getStackTrace()[7];
        String tag = String.format("%s:%d", element.getClassName(), element.getLineNumber());
        Matcher m = ANONYMOUS_CLASS.matcher(tag);
        if (m.find())
        {
            tag = m.replaceAll("");
        }
        return tag.substring(tag.lastIndexOf('.') + 1);
    }

    static String formatString(String message, Object... args)
    {
        // If no varargs are supplied, treat it as a request to log the string without formatting.
        return args.length == 0 ? message : String.format(message, args);
    }

    @Override public void v(String message, Object... args)
    {
        Log.v(getTag(), formatString(message, args));
    }

    @Override public void v(Throwable t, String message, Object... args)
    {
        Log.v(getTag(), formatString(message, args), t);
    }

    @Override public void d(String message, Object... args)
    {
        Log.d(getTag(), formatString(message, args));
    }

    @Override public void d(Throwable t, String message, Object... args)
    {
        Log.d(getTag(), formatString(message, args), t);
    }

    @Override public void i(String message, Object... args)
    {
        Log.i(getTag(), formatString(message, args));
    }

    @Override public void i(Throwable t, String message, Object... args)
    {
        Log.i(getTag(), formatString(message, args), t);
    }

    @Override public void w(String message, Object... args)
    {
        Log.w(getTag(), formatString(message, args));
    }

    @Override public void w(Throwable t, String message, Object... args)
    {
        Log.w(getTag(), formatString(message, args), t);
    }

    @Override public void e(String message, Object... args)
    {
        Log.e(getTag(), formatString(message, args));
    }

    @Override public void e(Throwable t, String message, Object... args)
    {
        Log.e(getTag(), formatString(message, args), t);
    }

    @Override public void tag(String tag)
    {
        NEXT_TAG.set(tag);
    }
}
