package com.tradehero.th.network;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UrlEncoderHelper
{
    static final boolean transform = false;

    public static String transform(String in)
    {
        if (transform)
        {
            Pattern p = Pattern.compile("\\s+|\t|\r|\n|#|&");
            Matcher m = p.matcher(in);
            String dest = m.replaceAll("-");
            return dest;
        }
        return in;
    }
}
