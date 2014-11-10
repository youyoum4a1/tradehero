package com.tradehero.th.utils;

import android.text.TextUtils;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    public static boolean containSpecialChars(String inputStr){
        if(TextUtils.isEmpty(inputStr)){
            return true;
        }
        String regEx = "[`~!@#$%^&*+=|{}':;',\\[\\].<>/?~！@#￥%……&*——+|{}【】‘；：”“’。，、？]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(inputStr);
        return m.find();
    }

}
