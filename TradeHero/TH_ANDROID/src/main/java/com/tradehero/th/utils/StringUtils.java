package com.ayondo.academy.utils;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import java.util.List;

public class StringUtils
{
    @Nullable public static String join(String glue, @Nullable List elements)
    {
        if (elements == null)
        {
            return null;
        }
        int length = elements.size();
        if (length == 0)
        {
            return null;
        }

        StringBuilder out = new StringBuilder();
        out.append(elements.get(0));
        for (int x = 1; x < length; ++x)
        {
            out.append(glue).append(elements.get(x));
        }
        return out.toString();
    }

    @Nullable public static String join(String glue, Object... elements)
    {
        if (elements == null)
        {
            return null;
        }
        int length = elements.length;
        if (length == 0)
        {
            return null;
        }

        StringBuilder out = new StringBuilder();
        out.append(elements[0]);
        for (int x = 1; x < length; ++x)
        {
            out.append(glue).append(elements[x]);
        }
        return out.toString();
    }

    public static boolean isNullOrEmpty(String str)
    {
        return str == null || str.isEmpty();
    }

    public static boolean isNullOrEmptyOrSpaces(String str)
    {
        return str == null || str.trim().isEmpty();
    }

    public static String removeImageSpanObjects(@NonNull String inStr)
    {
        SpannableStringBuilder spannedStr = (SpannableStringBuilder) Html
                .fromHtml(inStr.trim());
        Object[] spannedObjects = spannedStr.getSpans(0, spannedStr.length(),
                Object.class);
        for (int i = 0; i < spannedObjects.length; i++)
        {
            if (spannedObjects[i] instanceof ImageSpan)
            {
                ImageSpan imageSpan = (ImageSpan) spannedObjects[i];
                spannedStr.replace(spannedStr.getSpanStart(imageSpan),
                        spannedStr.getSpanEnd(imageSpan), "");
            }
        }
        return spannedStr.toString().trim();
    }
}
