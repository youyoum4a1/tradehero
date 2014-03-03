package com.tradehero.th.utils;

import java.util.List;

/** Created with IntelliJ IDEA. User: xavier Date: 11/14/13 Time: 5:06 PM To change this template use File | Settings | File Templates. */
public class StringUtils
{
    public static String join(String glue, List<String> elements)
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

    public static String join(String glue, String... elements)
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
