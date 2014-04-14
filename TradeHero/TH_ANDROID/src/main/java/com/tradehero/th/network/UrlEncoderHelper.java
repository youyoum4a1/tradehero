package com.tradehero.th.network;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.http.client.utils.URLEncodedUtils;

/**
 * Created by tradehero on 14-3-26.
 */
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
