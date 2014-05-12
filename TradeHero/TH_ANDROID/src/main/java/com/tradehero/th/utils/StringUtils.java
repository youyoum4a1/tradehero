package com.tradehero.th.utils;

import java.util.List;


public class StringUtils
{
    public static String join(String glue, List elements)
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

    public static String join(String glue, Object... elements)
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
}
